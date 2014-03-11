INSERT INTO `RIGHT_MENUITEM` VALUES ('11', '任务管理', 'taskview', 'framework.TaskController', 'admin', '2013-11-27 02:01:06', '', '', '100', '1', '1', '1', '', '21');
INSERT INTO `RIGHT_MENUITEM` VALUES ('12', '任务列表', 'taskPanel', 'taskPanel', 'admin', '2013-11-27 02:01:06', '', '', '100', '1', '1', '1', '', '11');
INSERT INTO `RIGHT_MENUITEM` VALUES ('13', '任务日志', 'taskLogPanel', 'taskLogPanel', 'admin', '2013-11-27 02:01:06', '', '', '100', '1', '1', '1', '', '11');

INSERT INTO `RIGHT_OPERATE` VALUES ('23', '暂停/恢复', 'admin', '2013-11-27 03:42:01', 'pauseTask', '0', '', '', '/task/triggerStatus.json', '12', '1');

INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('682', '1', '11');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('683', '1', '12');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('684', '1', '13');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('685', '100', '11');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('686', '100', '12');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('687', '100', '13');

INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('688', '125', '11');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('689', '125', '12');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('690', '125', '13');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('691', '120', '11');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('692', '120', '12');
INSERT INTO `RIGHT_ROLE_MENUITEM` VALUES ('693', '120', '13');

INSERT INTO `RIGHT_ROLE_OPERATE` VALUES ('547', '1', '23');
INSERT INTO `RIGHT_ROLE_OPERATE` VALUES ('548', '100', '23');
INSERT INTO `RIGHT_ROLE_OPERATE` VALUES ('549', '125', '23');
INSERT INTO `RIGHT_ROLE_OPERATE` VALUES ('550', '120', '23');


-- ----------------------------
-- Table structure for `QRTZ_LOG_INFO`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOG_INFO`;
CREATE TABLE `QRTZ_LOG_INFO` (
  `LOGID` int(11) NOT NULL AUTO_INCREMENT,
  `LEVEL` int(1) NOT NULL DEFAULT '3' COMMENT '日志级别：\r\n1 debug\r\n2 error\r\n3 info\r\n4 warm\r\n',
  `DESCRIPTION` varchar(500) DEFAULT NULL COMMENT '信息描述',
  `CREATETIME` datetime NOT NULL COMMENT '日志输出时间',
  PRIMARY KEY (`LOGID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_LOG_INFO
-- ----------------------------