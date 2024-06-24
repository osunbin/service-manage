package com.bin.webmonitor.common;

import com.bin.webmonitor.common.util.CollectionUtil;
import com.bin.webmonitor.component.StrJson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataGrid<T> extends StrJson implements Serializable {
    
    private static final long serialVersionUID = -2595781209418942623L;
    
    /**
     * 当前页面号
     */
    private int current = 0;
    
    /**
     * 每页行数
     */
    private int rowCount = 0;
    
    /**
     * 总行数
     */
    private int total = 0;
    
    private List<T> rows = new ArrayList<>();
    
    private Map<String, Object> searchMap = new HashMap<>();
    
    public DataGrid() {
    }
    
    public DataGrid(int current, int rowCount, int total, List<T> rows, Map<String, Object> searchMap) {
        this.current = current;
        this.rowCount = rowCount;
        this.total = total;
        if (Objects.nonNull(rows)) {
            this.rows = rows;
        }
        if (Objects.nonNull(searchMap)) {
            this.searchMap = searchMap;
        }
    }
    
    public int getCurrent() {
        return current;
    }
    
    public DataGrid<T> setCurrent(int current) {
        this.current = current;
        return this;
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public DataGrid<T> setRowCount(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }
    
    public int getTotal() {
        return total;
    }
    
    public DataGrid<T> setTotal(int total) {
        this.total = total;
        return this;
    }
    
    public List<T> getRows() {
        return rows;
    }
    
    public DataGrid<T> setRows(List<T> rows) {
        this.rows = rows;
        return this;
    }
    
    public Map<String, Object> getSearchMap() {
        return searchMap;
    }
    
    public DataGrid<T> setSearchMap(Map<String, Object> searchMap) {
        this.searchMap = searchMap;
        return this;
    }
    

    
    public static class DataGridBuilder<T> {
        private int current;
        
        private int rowCount;
        
        private int total;
        
        private List<T> rows;
        
        private Map<String, Object> searchMap;
        
        public DataGridBuilder<T> setCurrent(int current) {
            this.current = current;
            return this;
        }
        
        public DataGridBuilder<T> setRowCount(int rowCount) {
            this.rowCount = rowCount;
            return this;
        }
        
        public DataGridBuilder<T> setTotal(int total) {
            this.total = total;
            return this;
        }
        
        public DataGridBuilder<T> setRows(List<T> rows) {
            this.rows = rows;
            return this;
        }
        
        public DataGridBuilder<T> setSearchMap(Map<String, Object> searchMap) {
            this.searchMap = searchMap;
            return this;
        }
        
        public DataGrid<T> createDataGrid() {
            return new DataGrid<T>(current, rowCount, total, rows, searchMap);
        }
    }

    public static <T> DataGrid<T> genDataGrid(int page, int size, List<T> originList) {
        List<T> retList = CollectionUtil.selectByPage(page, size, originList);
        return new DataGrid.DataGridBuilder<T>()
                .setCurrent(page)
                .setRowCount(size)
                .setTotal(originList.size())
                .setRows(retList)
                .createDataGrid();
    }
}
