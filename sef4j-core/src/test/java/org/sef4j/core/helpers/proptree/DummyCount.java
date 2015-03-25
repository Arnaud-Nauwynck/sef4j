package org.sef4j.core.helpers.proptree;

import java.util.concurrent.Callable;

public class DummyCount {
    
    private int count1;
    private int count2;
    
    // ------------------------------------------------------------------------
    
    public DummyCount() {
    }
    
    public DummyCount(int count1, int count2) {
        super();
        this.count1 = count1;
        this.count2 = count2;
    }


    public static final Callable<DummyCount> FACTORY = new Callable<DummyCount>() {
        @Override
        public DummyCount call() throws Exception {
            return new DummyCount();
        }
    };

    // ------------------------------------------------------------------------

    public void getCopyTo(DummyCount dest) {
        dest.count1 = count1;
        dest.count2 = count2;
    }

    public DummyCount getCopy() {
        DummyCount res = new DummyCount();
        getCopyTo(res);
        return res;
    }

    public void setCopy(DummyCount src) {
        src.getCopyTo(this);
    }
    
    public int getCount1() {
        return count1;
    }

    public void setCount1(int count1) {
        this.count1 = count1;
    }

    public int getCount2() {
        return count2;
    }

    public void setCount2(int count2) {
        this.count2 = count2;
    }
    
    public void incrCount1() {
        this.count1++;
    }

    public void incrCount2() {
        this.count2++;
    }
    

}
