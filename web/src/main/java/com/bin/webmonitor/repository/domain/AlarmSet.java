package com.bin.webmonitor.repository.domain;

import java.util.Date;

/**
 *  报警设置
 */
public class AlarmSet {
    private Long id;

    /**
     * 报警id
     */
    private Long alarmId;

    /**
     * 每分钟最大阈值
     */
    private Integer maxMinute;

    /**
     * 每分钟最小阈值
     */
    private Integer minMinute;

    /**
     * 上分钟增加值
     */
    private Integer preMinuteUp;

    /**
     * 上分钟减少值
     */
    private Integer preMinuteDown;

    private Date startTime;

    private Date endTime;

    /**
     * 连续次数
     */
    private Byte contTimes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getMaxMinute() {
        return maxMinute;
    }

    public void setMaxMinute(Integer maxMinute) {
        this.maxMinute = maxMinute;
    }

    public Integer getMinMinute() {
        return minMinute;
    }

    public void setMinMinute(Integer minMinute) {
        this.minMinute = minMinute;
    }

    public Integer getPreMinuteUp() {
        return preMinuteUp;
    }

    public void setPreMinuteUp(Integer preMinuteUp) {
        this.preMinuteUp = preMinuteUp;
    }

    public Integer getPreMinuteDown() {
        return preMinuteDown;
    }

    public void setPreMinuteDown(Integer preMinuteDown) {
        this.preMinuteDown = preMinuteDown;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getContTimes() {
        return contTimes;
    }

    public void setContTimes(Byte contTimes) {
        this.contTimes = contTimes;
    }
}
