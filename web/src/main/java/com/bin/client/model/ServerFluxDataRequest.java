package com.bin.client.model;

import com.bin.collector.request.BaseData;
import com.bin.webmonitor.enums.FluxType;
import com.bin.webmonitor.enums.ReportType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerFluxDataRequest extends BaseData {
    /**
     * 统计时间
     */
    private long time;
    

    /**
     * 服务名
     */
    private String serviceName;
    
    /**
     * 一分钟内服务访问量
     */
    private List<ServerFluxData> serverFluxDatas;
    
    public ServerFluxDataRequest() {
        this.reportType = ReportType.SERVER_FLUXDATA;
        this.serverFluxDatas = new LinkedList<ServerFluxData>();
    }
    
    public ServerFluxDataRequest(long time, String serviceName, List<ServerFluxData> datas) {
        this.reportType = ReportType.SERVER_FLUXDATA;
        serverFluxDatas = datas;
        this.time = time;
        this.serviceName = serviceName;
    }
    
    @Override
    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2000);
        try {
            out.write(this.reportType.getValue());
           /* out.write(ByteUtil.getBytes(this.time));
           out.write(ByteUtil.getBytes(this.serviceName.length()));
            out.write(ByteUtil.getBytes(this.serviceName));*/
            for (ServerFluxData data : serverFluxDatas) {
                out.write(data.toByte());
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("ServerFluxDataRequest to byte error", e);
        }
        
    }
    
    public static final int PACKAGE_MAX_SIZE = 65000;
    
    /**
     * udp分包，如果大于65000则分包
     */
    public List<ServerFluxDataRequest> split() {
        LinkedList<ServerFluxDataRequest> serverFluxDataRequests = new LinkedList<ServerFluxDataRequest>();
        byte[] requestByte = this.toBytes();
        if (requestByte.length > PACKAGE_MAX_SIZE) {
            int packages = (int)Math.ceil(Double.valueOf(requestByte.length) / PACKAGE_MAX_SIZE);
            int dataEveryPackage = this.getServerFluxDatas().size() / packages;
            int j = 0;
            LinkedList<ServerFluxData> datas = new LinkedList<ServerFluxData>();
            for (int i = 0; i < this.getServerFluxDatas().size(); i++) {
                datas.add(this.serverFluxDatas.get(i));
                if (++j == dataEveryPackage) {
                    serverFluxDataRequests.add(new ServerFluxDataRequest(this.time, this.serviceName, datas));
                    datas = new LinkedList<>();
                    j = 0;
                }
            }
            if (datas.size() > 0) {
                serverFluxDataRequests.add(new ServerFluxDataRequest(this.time, this.serviceName, datas));
            }
            
        } else {
            serverFluxDataRequests.add(this);
        }
        return serverFluxDataRequests;
    }
    
    public Map<Long, AtomicInteger> getCallerCount() {
        
        ConcurrentHashMap<Long, AtomicInteger> callerCount = new ConcurrentHashMap<Long, AtomicInteger>(this.getServerFluxDatas().size());
        for (ServerFluxData a : this.getServerFluxDatas()) {
            callerCount.putIfAbsent(a.getCallerKeyId(), new AtomicInteger(0));
            callerCount.get(a.getCallerKeyId()).getAndAdd(a.getValue());
        }
        return callerCount;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long time) {
        this.time = time;
    }
    

    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public List<ServerFluxData> getServerFluxDatas() {
        return serverFluxDatas;
    }
    
    public void setServerFluxDatas(List<ServerFluxData> serverFluxDatas) {
        this.serverFluxDatas = serverFluxDatas;
    }
    
    @Override
    public String toString() {
        return "ServerFluxDataRequest{" + "time=" + time +  ", serviceName='" + serviceName + '\'' + ", serverFluxDatas=" + serverFluxDatas + '}';
    }
    
    @Override
    public ServerFluxDataRequest toRequest(byte[] b) {
        this.serverFluxDatas.clear();
        if (null == b) {
            return null;
        }
        
        int currentIndex = 0;
        this.reportType = ReportType.parse(b[currentIndex]);
        currentIndex += 1;
      //  this.time = ByteConverter.bytesToLongLittleEndian(b, currentIndex);
        currentIndex += 8;
        int serviceNameLength = b[currentIndex];
        currentIndex += 1;
        byte[] svcName = new byte[serviceNameLength];
        System.arraycopy(b, currentIndex, svcName, 0, serviceNameLength);
        this.serviceName = new String(svcName);
        currentIndex += serviceNameLength;
        
        while (b.length - currentIndex + 1 >= 29) {
            /*long funcId = ByteConverter.bytesToLongLittleEndian(b, currentIndex);
            currentIndex += 8;
            long callerKeyId = ByteConverter.bytesToLongLittleEndian(b, currentIndex);
            currentIndex += 8;
            byte flag = b[currentIndex];
            currentIndex += 1;
            int value = ByteConverter.bytesToIntLittleEndian(b, currentIndex);
            currentIndex += 4;
            int totalCost = ByteConverter.bytesToIntLittleEndian(b, currentIndex);
            currentIndex += 4;
            int executeCost = ByteConverter.bytesToIntLittleEndian(b, currentIndex);
            currentIndex += 4;
            ServerFluxData fluxData = new ServerFluxData(funcId, callerKeyId, FluxType.parse(flag), value, totalCost, executeCost);
            this.serverFluxDatas.add(fluxData);*/
        }
        return this;
    }
    
    /**
     * @类名称 ServerFluxData.java
     * @类描述 服务流量数据对象
     * @版本 1.0.0
     *
     * @修改记录
     * <pre>
     *     版本                       修改人 		修改日期 		 修改内容描述
     *     ----------------------------------------------
     *     1.0.0 		庄梦蝶殇 	2020年5月16日             
     *     ----------------------------------------------
     * </pre>
     */
    public static class ServerFluxData {
        /**
         * 方法id
         */
        private long functionId;
        
        /**
         * 调用者id
         */
        private long callerKeyId;

        
        /**
         * 调用次数(累计)
         */
        private AtomicInteger value;
        

        
        public ServerFluxData(long functionId, long callerKeyId, FluxType flag, int value, int totalCost, int executeCost) {
            this.functionId = functionId;
            this.callerKeyId = callerKeyId;
            this.value = new AtomicInteger(value);

        }
        
        public byte[] toByte() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
              /*  out.write(ByteUtil.getBytes(this.functionId));
                out.write(ByteUtil.getBytes(this.callerKeyId));
                out.write(ByteUtil.getBytes(this.value.get()));*/
                return out.toByteArray();
            } catch (Throwable e) {
                throw new RuntimeException("", e);
            }
        }
        
        public void addCount() {
            this.value.addAndGet(1);
        }
        

        
        public long getFunctionId() {
            return functionId;
        }
        
        public void setFunctionId(long functionId) {
            this.functionId = functionId;
        }
        
        public long getCallerKeyId() {
            return callerKeyId;
        }
        
        public void setCallerKeyId(long callerKeyId) {
            this.callerKeyId = callerKeyId;
        }
        

        
        public int getValue() {
            return value.get();
        }
        
        public AtomicInteger getAtomicValue() {
            return value;
        }
        

        @Override
        public String toString() {
            return "ServerFluxData{" + "functionId=" + functionId + ", callerKeyId=" + callerKeyId +  ", value=" + value +'}';
        }
        
    }
    
}