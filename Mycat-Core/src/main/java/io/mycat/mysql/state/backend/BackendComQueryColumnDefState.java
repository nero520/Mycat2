package io.mycat.mysql.state.backend;


import java.io.IOException;

import io.mycat.mysql.state.PacketProcessStateTemplete;
import io.mycat.net2.Connection;

import io.mycat.backend.MySQLBackendConnection;

/**
 * 列定义传送状态
 *
 * @author ynfeng
 */
public class BackendComQueryColumnDefState extends PacketProcessStateTemplete {
    public static final BackendComQueryColumnDefState INSTANCE = new BackendComQueryColumnDefState();


    private BackendComQueryColumnDefState() {
    }


    @Override
    public boolean handleShortHalfPacket(Connection connection, Object attachment, int packetStartPos) throws IOException {
        return false;
    }

    @Override
    public boolean handleLongHalfPacket(Connection connection, Object attachment, int packetStartPos, int packetLen, byte type) throws IOException {
        MySQLBackendConnection mySQLBackendConnection = (MySQLBackendConnection) connection;
        mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
        return false;
    }

    @Override
    public boolean handleFullPacket(Connection connection, Object attachment, int packetStartPos, int packetLen, byte type) throws IOException {
        MySQLBackendConnection mySQLBackendConnection = (MySQLBackendConnection) connection;
        if (type == (byte) 0xfe) {
            mySQLBackendConnection.getProtocolStateMachine().setNextState(BackendComQueryRowState.INSTANCE);
            interruptIterate();
            return true;
        }
        //TODO bug!!!此处如果不透传，刚好buffer满了，网络状态机会一直卡在读状
        mySQLBackendConnection.startTransfer(mySQLBackendConnection.getMySQLFrontConnection(), mySQLBackendConnection.getDataBuffer());
        return false;
    }
}
