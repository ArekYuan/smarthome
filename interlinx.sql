/*
 Navicat Premium Data Transfer

 Source Server         : interlinx
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : 106.12.14.32:3306
 Source Schema         : interlinx

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 02/07/2019 14:32:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auth
-- ----------------------------
DROP TABLE IF EXISTS `auth`;
CREATE TABLE `auth`  (
  `auth_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userid` int(10) NOT NULL COMMENT '用户id',
  `device_id` int(10) NOT NULL COMMENT '设备id',
  `feedback_id` int(10) NOT NULL COMMENT '反馈id',
  `userinfo_id` int(10) NOT NULL COMMENT '用户详情id',
  PRIMARY KEY (`auth_id`) USING BTREE,
  UNIQUE INDEX `userid`(`userid`, `device_id`, `feedback_id`, `userinfo_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `device_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '产品id',
  `userid` int(10) DEFAULT NULL COMMENT '用户id',
  `small_imgurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品item的图片',
  `large_imgurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品明细大图',
  `desc` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品描述',
  `take_off` int(2) DEFAULT NULL COMMENT '开关-->0：关  1：开',
  `mode` int(2) DEFAULT NULL COMMENT '智能模式-->0：连续运转模式  1：智能模式',
  `lock` int(2) DEFAULT NULL COMMENT '键盘锁-->0：解锁  1：上锁',
  `wind_power` int(3) DEFAULT NULL COMMENT '风力强度-->1：弱，2：中，3：强',
  `air_lamp` int(5) DEFAULT NULL COMMENT '氛围灯-->0：关闭，1：弱，2：中，3：强',
  `timing_set` int(5) DEFAULT NULL COMMENT '定时设置-->0：无定时，1：3小时，2：6小时，3：12小时',
  `water_level` int(5) DEFAULT NULL COMMENT '水位值-->0-1000',
  `status` int(2) DEFAULT NULL COMMENT '0：在线，1：下线（异常）',
  `lat` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '经度',
  `lng` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '维度',
  `altitude` int(10) DEFAULT NULL COMMENT '高程',
  `device_mac` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'mac 地址',
  `wifi_mac` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '路由mac地址',
  PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device
-- ----------------------------
INSERT INTO `device` VALUES (1, 1, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `device` VALUES (2, 1, NULL, NULL, NULL, 0, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `device` VALUES (4, 1, NULL, NULL, '杀菌器', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `feedback_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `userid` int(10) NOT NULL COMMENT '用户id\r\n',
  `repair_img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '保修图片，以 | 隔开',
  `feedback` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '保修描述',
  `repair_time` datetime(0) DEFAULT NULL COMMENT '保修时间',
  `repair_phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '报修人电话',
  PRIMARY KEY (`feedback_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo`  (
  `userid` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '密码',
  `address` varchar(1000) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '收货地址\r\n',
  `cardnum` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '银行卡号',
  `idcard` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `is_admin` int(2) DEFAULT NULL COMMENT '是否可对管理系统进行编辑',
  `last_login_time` datetime(0) DEFAULT NULL COMMENT '最近一次登录时间',
  `login_out` int(2) DEFAULT NULL COMMENT '是否下线',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `imgurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `openid` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `createtime` datetime(0) DEFAULT NULL,
  `mac` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES (1, 'BigBoy', '15351975925', '123456', '阿达', '6217232365284082', '513701199109284915', 1, '2019-05-31 22:42:34', 0, '男', NULL, 'wx132513asdadd132', '2019-05-16 22:42:47', 'asdasd');

SET FOREIGN_KEY_CHECKS = 1;
