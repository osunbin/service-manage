package com.bin.webmonitor.repository.keys;

/**
 * 数据收集对象的抽象父类，子类必须有生成字符串key的能力
 */
public abstract class BaseCollectorKey implements Comparable<BaseCollectorKey>, FluxKey {

    private String key;

    public abstract String toKey();

    public abstract String desc();

    public abstract String timeCostdesc();

    public abstract String timeRealCostdesc();

    protected CollectorKey collectorKey;

    @Override
    public abstract BaseCollectorKey gen(CollectorKey collectorKey);

    public BaseCollectorKey() {
    }

    public String getKey() {
        if (key != null) {
            return key;
        }

        synchronized (this) {
            if (key == null) {
                key = toKey();
            }
            return key;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseCollectorKey other = (BaseCollectorKey) obj;
        if (getKey() == null) {
            if (other.getKey() != null) {
                return false;
            }
        } else if (!getKey().equals(other.getKey())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BaseCollectorKey other) {
        return this.getKey().compareTo(other.getKey());
    }

    public CollectorKey getCollectorKey() {
        return collectorKey;
    }

    public BaseCollectorKey setCollectorKeyDto(CollectorKey collectorKey) {
        this.collectorKey = collectorKey;
        return this;
    }


}
