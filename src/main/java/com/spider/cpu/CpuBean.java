package com.spider.cpu;

/**
 * @author javakam
 * @apiNote 2024-12-23 15:43:27
 */
public class CpuBean {
    private int cpuId;
    private String name;//名称
    private String performanceRank;//性能排名
    private String score;//得分
    private String tdp;//TDP
    private String tdpDown;//TDP Down
    private String slotType;//插槽类型
    private String coreNumber;//核心数
    private String threadNumber;//线程数
    private String frequency;//主频
    private String turboFrequency;//睿频
    private String released;//发布时间

    private String initialPrice;//首发价格
    private String historyLowPrice;//史低价格
    private String latestPrice;//最新价格

    public int getCpuId() {
        return cpuId;
    }

    public void setCpuId(int cpuId) {
        this.cpuId = cpuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerformanceRank() {
        return performanceRank;
    }

    public void setPerformanceRank(String performanceRank) {
        this.performanceRank = performanceRank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTdp() {
        return tdp;
    }

    public void setTdp(String tdp) {
        this.tdp = tdp;
    }

    public String getTdpDown() {
        return tdpDown;
    }

    public void setTdpDown(String tdpDown) {
        this.tdpDown = tdpDown;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getCoreNumber() {
        return coreNumber;
    }

    public void setCoreNumber(String coreNumber) {
        this.coreNumber = coreNumber;
    }

    public String getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(String threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTurboFrequency() {
        return turboFrequency;
    }

    public void setTurboFrequency(String turboFrequency) {
        this.turboFrequency = turboFrequency;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }

    public String getHistoryLowPrice() {
        return historyLowPrice;
    }

    public void setHistoryLowPrice(String historyLowPrice) {
        this.historyLowPrice = historyLowPrice;
    }

    public String getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(String latestPrice) {
        this.latestPrice = latestPrice;
    }

    @Override
    public String toString() {
        return "CpuBean{" +
                "cpuId=" + cpuId +
                ", name='" + name + '\'' +
                ", performanceRank='" + performanceRank + '\'' +
                ", score='" + score + '\'' +
                ", tdp='" + tdp + '\'' +
                ", tdpDown='" + tdpDown + '\'' +
                ", slotType='" + slotType + '\'' +
                ", coreNumber='" + coreNumber + '\'' +
                ", threadNumber='" + threadNumber + '\'' +
                ", frequency='" + frequency + '\'' +
                ", turboFrequency='" + turboFrequency + '\'' +
                ", released='" + released + '\'' +
                ", initialPrice='" + initialPrice + '\'' +
                ", historyLowPrice='" + historyLowPrice + '\'' +
                ", latestPrice='" + latestPrice + '\'' +
                '}';
    }
}
