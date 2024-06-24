SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_caller
-- ----------------------------
DROP TABLE IF EXISTS `t_caller`;
CREATE TABLE `t_caller`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `callerName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调用者名称',
  `owners` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用负责人',
  `description` varchar(1023) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '注册描述',
  `createTime` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `updateTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `callerKey` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '调用者秘钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `callerName`(`callerName`) USING BTREE,
  UNIQUE INDEX `uk_callerKey`(`callerKey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务调用者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_callerfunctionuseage
-- ----------------------------
DROP TABLE IF EXISTS `t_callerfunctionuseage`;
CREATE TABLE `t_callerfunctionuseage`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `fid` int(0) NOT NULL COMMENT '函数 id',
  `quantity` int(0) NOT NULL DEFAULT 0 COMMENT '函数申请的调用量',
  `timeout` int(0) NOT NULL DEFAULT 0 COMMENT '函数超时时间，单位：毫秒',
  `fname` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '函数名',
  `createTime` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `updateTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `degrade` tinyint(0) NULL DEFAULT 0 COMMENT '函数降级 0不降级 1 降级',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `cid`(`cid`, `fid`) USING BTREE,
  INDEX `idx_sid`(`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调用者调用函数的属性表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_callerspecial
-- ----------------------------
DROP TABLE IF EXISTS `t_callerspecial`;
CREATE TABLE `t_callerspecial`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `cips` varchar(1023) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '调用方 ip 列表',
  `attrJson` varchar(511) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '格式{\"Serialize\":\"\",\"sips\":\"\"}',
  `createTime` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `updateTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cid`(`cid`) USING BTREE,
  INDEX `idx_sid`(`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_calleruseage
-- ----------------------------
DROP TABLE IF EXISTS `t_calleruseage`;
CREATE TABLE `t_calleruseage`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `gid` int(0) NOT NULL DEFAULT 0 COMMENT '分组id，对应t_group的id, 0代表不分组',
  `quantity` int(0) NOT NULL DEFAULT 0 COMMENT '每分钟调用量',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '调用描述',
  `protocolSerialize` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `protocolCompressType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `protocolProtocolType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `serverDeadTimeout` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `degrade` tinyint(0) NOT NULL DEFAULT 0,
  `reject` tinyint(0) NOT NULL DEFAULT 1 COMMENT '服务端拒绝：0不拒绝1拒绝',
  `createTime` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `updateTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `granularity` tinyint(0) NOT NULL DEFAULT 0 COMMENT '粒度。0函数级；1服务级。',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `u_k_cid_sid`(`cid`, `sid`) USING BTREE,
  INDEX `idx_sid`(`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调用者配置信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_calleruseageext
-- ----------------------------
DROP TABLE IF EXISTS `t_calleruseageext`;
CREATE TABLE `t_calleruseageext`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `config_type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '配置来源。0：未知；1：服务管理平台；2：本地配置。',
  `create_time` timestamp(0) NOT NULL DEFAULT '2019-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cid_sid`(`cid`, `sid`) USING BTREE,
  INDEX `idx_sid`(`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调用关系扩展信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_circuit_break_config
-- ----------------------------
DROP TABLE IF EXISTS `t_circuit_break_config`;
CREATE TABLE `t_circuit_break_config`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `fid` int(0) NOT NULL COMMENT '函数 id',
  `enabled` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否开启熔断机制。0：不是；1：是。',
  `force_opened` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否强制开启熔断机制。0：不是；1：是。',
  `force_closed` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否强制关闭熔断机制。0：不是；1：是。',
  `slide_window_in_seconds` int(0) NOT NULL COMMENT '滑动窗口时长（秒)',
  `request_volume_threshold` int(0) NOT NULL COMMENT '窗口内进行熔断判断的最小请求个数（请求数过少则不会启动熔断机制）',
  `error_threshold_percentage` tinyint(0) NOT NULL COMMENT '错误比例，百分比，取值范围为[0,100]',
  `sleep_window_in_milliseconds` int(0) NOT NULL COMMENT '熔断时间窗口时长（毫秒)',
  `create_time` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_cid_sid_fid`(`cid`, `sid`, `fid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '熔断配置参数表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_circuit_break_event
-- ----------------------------
DROP TABLE IF EXISTS `t_circuit_break_event`;
CREATE TABLE `t_circuit_break_event`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `fid` int(0) NOT NULL COMMENT '函数 id',
  `ip` int(0) UNSIGNED NOT NULL COMMENT '调用方IP',
  `event_type` tinyint(0) NOT NULL COMMENT '事件类型。0：熔断关闭；1：熔断打开；2：client重新启动。',
  `event_time` bigint(0) NOT NULL COMMENT '时件发生时的毫秒时间',
  `reason` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '事件发生原因',
  `create_time` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cid_sid_fid_ip`(`cid`, `sid`, `fid`, `ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '熔断事件记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_circuit_break_monitor
-- ----------------------------
DROP TABLE IF EXISTS `t_circuit_break_monitor`;
CREATE TABLE `t_circuit_break_monitor`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用方 id',
  `sid` int(0) NOT NULL COMMENT '服务方 id',
  `fid` int(0) NOT NULL COMMENT '函数 id',
  `ip` int(0) UNSIGNED NOT NULL COMMENT '调用方IP',
  `success_count` int(0) NOT NULL COMMENT '成功数',
  `fail_count` int(0) NOT NULL COMMENT '失败数',
  `timeout_count` int(0) NOT NULL COMMENT '超时数',
  `status` tinyint(0) NOT NULL COMMENT '熔断器状态。0：关闭；1：打开。',
  `min_time` bigint(0) NOT NULL COMMENT '上报时间分钟数的毫秒时间',
  `create_time` timestamp(0) NOT NULL DEFAULT '2018-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_cid_sid_fid_ip`(`cid`, `sid`, `fid`, `ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '熔断监控数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_clientconfig
-- ----------------------------
DROP TABLE IF EXISTS `t_clientconfig`;
CREATE TABLE `t_clientconfig`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `cid` int(0) NOT NULL COMMENT '调用者秘钥',
  `ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用者ip',
  `usage_config` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调用方配置信息',
  `create_time` timestamp(0) NOT NULL DEFAULT '2019-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cid_ip`(`cid`, `ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调用方配置信息表' ROW_FORMAT = Dynamic;



-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `sid` int(0) NOT NULL,
  `groupName` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `status` int(0) NULL DEFAULT 0,
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sid`(`sid`, `groupName`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务分组信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_nodegroup
-- ----------------------------
DROP TABLE IF EXISTS `t_nodegroup`;
CREATE TABLE `t_nodegroup`  (
  `gid` int(0) NULL DEFAULT NULL,
  `ip` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  UNIQUE INDEX `gid`(`gid`, `ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务节点分组信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_operaterecord
-- ----------------------------
DROP TABLE IF EXISTS `t_operaterecord`;
CREATE TABLE `t_operaterecord`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `uid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户uid，如huangwei',
  `cid` int(0) NOT NULL DEFAULT 0 COMMENT '调用者id',
  `sid` int(0) NOT NULL DEFAULT 0 COMMENT '服务id',
  `op_type` int(0) NOT NULL COMMENT '操作类型，1xxx表示调用方，2xxx表示服务方，3xxx表示调用关系',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作内容',
  `create_time` timestamp(0) NOT NULL DEFAULT '2019-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cid`(`cid`) USING BTREE,
  INDEX `idx_sid`(`sid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务管理平台操作表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for t_service
-- ----------------------------
DROP TABLE IF EXISTS `t_service`;
CREATE TABLE `t_service`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `serviceName` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `owners` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `tcpPort` int(0) NOT NULL,
  `telnetPort` int(0) NULL DEFAULT NULL,
  `allowServiceGranularity` tinyint(0) NULL DEFAULT 0,
  `limitAlarm` tinyint(0) NULL DEFAULT 0 COMMENT '0,限流不报警 1，限流报警',
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_servicename`(`serviceName`) USING BTREE,
  UNIQUE INDEX `uk_tcpport`(`tcpPort`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '服务信息表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for t_serviceconfig
-- ----------------------------
DROP TABLE IF EXISTS `t_serviceconfig`;
CREATE TABLE `t_serviceconfig`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `sid` int(0) NOT NULL COMMENT '服务名称',
  `ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务的ip地址',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务的配置信息',
  `log4j` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务的日志配置信息',
  `ext` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '服务的附加信息',
  `create_time` timestamp(0) NOT NULL DEFAULT '2019-01-01 00:00:00' COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sid_ip`(`sid`, `ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_servicefunction
-- ----------------------------
DROP TABLE IF EXISTS `t_servicefunction`;
CREATE TABLE `t_servicefunction`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `sid` int(0) NOT NULL,
  `fname` varbinary(800) NOT NULL,
  `interface` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '函数接口名',
  `lookup` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '函数实现类名',
  `generic_signature` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '带有泛型的函数签名',
  `general_signature` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '通用函数签名',
  `multi_impl` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否使用多实现，默认：否',
  `sfgid` int(0) NULL DEFAULT -1 COMMENT '服务函数分组 其中-1为默认分组',
  `ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_sid`(`sid`, `fname`(767)) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '服务方法信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_servicefunctiongroup
-- ----------------------------
DROP TABLE IF EXISTS `t_servicefunctiongroup`;
CREATE TABLE `t_servicefunctiongroup`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `sid` int(0) NOT NULL,
  `groupName` varchar(50) CHARACTER SET sjis COLLATE sjis_japanese_ci NOT NULL,
  `createTime` timestamp(0) NULL DEFAULT NULL,
  `updateTime` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_sidgroup`(`sid`, `groupName`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务函数分组信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
