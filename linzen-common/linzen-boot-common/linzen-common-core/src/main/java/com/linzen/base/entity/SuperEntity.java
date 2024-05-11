package com.linzen.base.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public abstract class SuperEntity<T> extends SuperBaseEntity.SuperCUDBaseEntity<T> {




}
