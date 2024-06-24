package com.bin.client.comfig;

import com.bin.webmonitor.common.util.JsonHelper;
import com.bin.webmonitor.common.util.StringUtil;
import com.bin.webmonitor.externapi.ServiceResult;
import com.bin.webmonitor.model.ServerConfig;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    
    private static String localConfigPath = File.separator + "tmp" + File.separator + "platform" + File.separator;
    
    public static void init(String callerKey) {
        if (StringUtil.isBlank(callerKey)) {
            callerKey = "callerKey";
        }
        String os = System.getProperty("os.name");
        if (!StringUtil.isBlank(os)) {
            os = os.toLowerCase();
            if (os.contains("win")) {
                String dir = System.getProperty("user.dir");
                
                if (!StringUtil.isBlank(dir)) {
                    try {
                        String disk = dir.split(":")[0];
                        localConfigPath = disk + ":" + localConfigPath + callerKey;
                    } catch (Throwable e) {
                    }
                }
            } else {
                localConfigPath = localConfigPath + callerKey;
            }
        }
    }
    
    public synchronized static ServiceResult<ServerConfig> loadServiceConfig(String serviceName) {
        BufferedReader reader = null;
        try {
            String filePath = localConfigPath + File.separator + serviceName + ".config";
            File f = new File(filePath);
            if (!f.exists()) {
                return null;
            }
            reader = new BufferedReader(new FileReader(filePath));
            String config = reader.readLine();
            return JsonHelper.fromJson(config, new TypeToken<ServiceResult<ServerConfig>>() {
            }.getType());
        } catch (Exception e) {
            logger.info("[ARCH_SDK_ignore_loadServiceConfig]serviceName={}", serviceName, e);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
    
    public synchronized static void saveServiceConfig(String serviceName, ServiceResult<ServerConfig> configServiceResult) {
        String filePath = localConfigPath + File.separator + serviceName + ".config";
        File f = new File(filePath);
        try {
            if (!f.exists()) {
                try {
                    File p = new File(f.getParent());
                    if (!p.exists()) {
                        p.mkdirs();
                    }
                } catch (Throwable e) {
                }
                
                f.createNewFile();
            }
            BufferedWriter writer = null;
            try {
                String strConfig = JsonHelper.toJson(configServiceResult);
                writer = new BufferedWriter(new FileWriter(filePath));
                writer.write(strConfig);
                writer.flush();
                logger.info("[ARCH_SDK_end_saveServiceConfig]serviceName={},filePath={},ConfigServiceResult={}", serviceName, filePath, configServiceResult);
            } catch (Throwable e) {
                logger.error("[ARCH_SDK_error_saveServiceConfig]serviceName={},filePath={},configServiceResult={}", serviceName, filePath, configServiceResult, e);
            } finally {
                try {
                    if (null != writer) {
                        writer.close();
                    }
                } catch (Exception e) {
                    
                }
            }
        } catch (IOException e) {
            logger.error("[ARCH_SDK_error_saveServiceConfig]serviceName={},filePath={},configServiceResult={}", serviceName, filePath, configServiceResult, e);
        }
    }
}