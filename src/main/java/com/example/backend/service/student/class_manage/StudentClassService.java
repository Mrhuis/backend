package com.example.backend.service.student.class_manage;

/**
 * 学生端班级管理服务接口
 */
public interface StudentClassService {
    /**
     * 学生通过邀请码加入班级
     *
     * @param inviteCode 邀请码
     * @param userKey 学生userKey
     * @return 是否加入成功
     */
    boolean joinClassByInviteCode(String inviteCode, String userKey);

    /**
     * 学生退出班级
     *
     * @param classKey 班级标识
     * @param userKey 学生userKey
     * @return 是否退出成功
     */
    boolean leaveClass(String classKey, String userKey);
}

