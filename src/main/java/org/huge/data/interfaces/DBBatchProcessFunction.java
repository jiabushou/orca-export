package org.huge.data.interfaces;

public interface DBBatchProcessFunction <T, R>{

    R process(T t);
}
