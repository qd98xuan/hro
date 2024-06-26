package com.linzen.scheduletask.model;

import lombok.Data;

/**
 *
 * @author FHNP
 * @version V0.0.1
 * @copyright 领致信息技术有限公司
 * @date 2023-04-01
 */
@Data
public class TaskLogVO {
   private String description;

  private String id;

  private Integer runResult;

   private Long runTime;
}
