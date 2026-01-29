package com.hrms.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Utility class quản lý Distributed Transaction giữa 2 CSDL
 * Đảm bảo tính toàn vẹn dữ liệu khi thao tác trên cả DB1 và DB2
 */
public class DistributedTransactionManager {
    
    private Connection db1Connection;
    private Connection db2Connection;
    private boolean isActive = false;
    private Savepoint db1Savepoint;
    private Savepoint db2Savepoint;
    
    /**
     * Bắt đầu distributed transaction
     */
    public void beginTransaction() throws SQLException {
        try {
            db1Connection = DatabaseConnection.getDB1Connection();
            db1Connection.setAutoCommit(false);
            db1Savepoint = db1Connection.setSavepoint("DB1_SAVEPOINT");
            
            if (DatabaseConnection.canAccessDB2()) {
                db2Connection = DatabaseConnection.getDB2Connection();
                db2Connection.setAutoCommit(false);
                db2Savepoint = db2Connection.setSavepoint("DB2_SAVEPOINT");
            }
            
            isActive = true;
        } catch (SQLException e) {
            rollback();
            throw new SQLException("Không thể bắt đầu distributed transaction: " + e.getMessage());
        }
    }
    
    /**
     * Commit tất cả thay đổi trên cả 2 CSDL
     */
    public void commit() throws SQLException {
        if (!isActive) {
            throw new SQLException("Không có transaction đang hoạt động");
        }
        
        try {
            // Commit DB1 trước
            if (db1Connection != null) {
                db1Connection.commit();
            }
            
            // Commit DB2
            if (db2Connection != null) {
                db2Connection.commit();
            }
            
            isActive = false;
        } catch (SQLException e) {
            // Nếu commit thất bại, rollback tất cả
            rollback();
            throw new SQLException("Lỗi commit distributed transaction: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }
    
    /**
     * Rollback tất cả thay đổi trên cả 2 CSDL
     */
    public void rollback() {
        try {
            if (db1Connection != null && !db1Connection.isClosed()) {
                if (db1Savepoint != null) {
                    db1Connection.rollback(db1Savepoint);
                } else {
                    db1Connection.rollback();
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi rollback DB1: " + e.getMessage());
        }
        
        try {
            if (db2Connection != null && !db2Connection.isClosed()) {
                if (db2Savepoint != null) {
                    db2Connection.rollback(db2Savepoint);
                } else {
                    db2Connection.rollback();
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi rollback DB2: " + e.getMessage());
        }
        
        isActive = false;
        closeConnections();
    }
    
    /**
     * Đóng tất cả kết nối
     */
    private void closeConnections() {
        DatabaseConnection.closeConnection(db1Connection);
        DatabaseConnection.closeConnection(db2Connection);
        db1Connection = null;
        db2Connection = null;
        db1Savepoint = null;
        db2Savepoint = null;
    }
    
    /**
     * Lấy kết nối DB1 trong transaction
     */
    public Connection getDB1Connection() throws SQLException {
        if (!isActive) {
            throw new SQLException("Transaction chưa được bắt đầu");
        }
        return db1Connection;
    }
    
    /**
     * Lấy kết nối DB2 trong transaction
     */
    public Connection getDB2Connection() throws SQLException {
        if (!isActive) {
            throw new SQLException("Transaction chưa được bắt đầu");
        }
        if (db2Connection == null) {
            throw new SQLException("Không có quyền truy cập DB2");
        }
        return db2Connection;
    }
    
    /**
     * Kiểm tra transaction có đang hoạt động không
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Thực hiện một thao tác trong distributed transaction
     */
    public static void executeInTransaction(TransactionCallback callback) throws SQLException {
        DistributedTransactionManager txManager = new DistributedTransactionManager();
        try {
            txManager.beginTransaction();
            callback.execute(txManager);
            txManager.commit();
        } catch (SQLException e) {
            txManager.rollback();
            throw e;
        }
    }
    
    /**
     * Interface callback cho transaction
     */
    @FunctionalInterface
    public interface TransactionCallback {
        void execute(DistributedTransactionManager txManager) throws SQLException;
    }
}
