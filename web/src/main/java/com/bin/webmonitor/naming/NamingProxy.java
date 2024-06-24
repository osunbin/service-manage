package com.bin.webmonitor.naming;

import com.bin.webmonitor.naming.model.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NamingProxy {



    public Response<?> execute(GiveCommand command) {
        return null;
    }



    public Response<List<ServiceNode>> getAllNodes() {
        return null;
    }

    /**
     * @方法名称 getNodes
     * @功能描述 获取 某个服务的服务节点
     * @param serviceName 服务名称
     * @return 服务节点对象列表
     */
    public Response<List<ServiceNode>> getNodes(String serviceName) {
        return null;
    }

    /**
     * @方法名称 getNodes
     * @功能描述 获取 某个调用者名下的服务节点
     * @param callerKey 调用者标识
     * @return 服务节点对象列表
     */
    public Response<List<ServiceNode>> getCallerNodes(String callerKey) {
        return null;
    }


    /**
     * @方法名称 batchQuery
     * @功能描述 获取某个集群下的服务节点
     * @param cluster 集群对象
     * @return 服务节点对象列表
     */
    public Response<List<ServiceNode>> batchQuery(ServiceCluster cluster){
        return null;
    }


    /**
     * @方法名称 batchOpen
     * @功能描述 打开某个集群下的服务节点
     * @param cluster 集群对象
     * @return
     */
    public Response<?> batchOpen(ServiceCluster cluster){
        return null;
    }

    /**
     * @方法名称 batchClose
     * @功能描述 关闭某个集群下的服务节点
     * @param cluster 集群对象
     * @return
     */
    public Response<?> batchClose(ServiceCluster cluster) {
        return null;
    }

    /**
     * @方法名称 batchDelete
     * @功能描述 删除某个集群下的服务节点
     * @param cluster 集群对象
     * @return 操作结果
     */
    public Response<?> batchDelete(ServiceCluster cluster) {
        return null;
    }



    /**
     * @方法名称 updateNodeWeight
     * @功能描述 更改服务节点权重
     * @param nodeWeight
     * @return
     */
    public Response<?> updateNodeWeight(ServiceNodeWeight nodeWeight) {
        return null;
    }

    /**
     * @方法名称 getServiceNodeByIp
     * @功能描述 更加ip获取服务节点列表
     * @param ip
     * @return
     */
    public Response<?> getServiceNodeByIp(String ip) {
        return null;
    }
}
