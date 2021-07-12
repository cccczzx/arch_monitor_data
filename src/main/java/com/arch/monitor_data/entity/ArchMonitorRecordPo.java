package com.arch.monitor_data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@ToString
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table (name="arch_monitor_record")
@TableName (value = "arch_monitor_record")
public class ArchMonitorRecordPo implements Serializable {

    private static final long serialVersionUID = -7167379749751710795L;

    /**
     * 主键
     * */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;

    /**
     * 采集的文件名
     * */
    @Column(name="file_name")
    private String fileName ;

    /**
     * 创建时间
     * */
    @Column(name="create_time")
    private Date createTime ;

    /**
     * 更新时间
     * */
    @Column(name="update_time")
    private Date updateTime ;



}
