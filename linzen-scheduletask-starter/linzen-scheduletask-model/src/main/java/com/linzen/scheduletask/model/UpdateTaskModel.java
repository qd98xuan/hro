package com.linzen.scheduletask.model;

import com.linzen.base.UserInfo;
import com.linzen.scheduletask.entity.TimeTaskEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateTaskModel implements Serializable {

    private TimeTaskEntity entity;

    private UserInfo userInfo;

}
