package com.labs.dm.haselnuss.server.tcp;

import com.labs.dm.haselnuss.Consts;
import com.labs.dm.haselnuss.server.tcp.command.Command;
import com.labs.dm.haselnuss.server.tcp.command.Response;
import org.junit.Test;

import java.io.IOException;
import java.net.BindException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author daniel
 */
public class TcpServerTest {

    @Test
    public void testRunServer() throws Exception {
        TcpServer instance = new TcpServer();
        instance.runServer();

        try (TcpConnection connection = new TcpConnection("localhost", 6543)) {
            connection.connect();
            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.close();
    }

    @Test
    public void testLoadFromProperties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("tcp.port", "9876");
        TcpServer instance = new TcpServer(properties);
        instance.runServer();

        try (TcpConnection connection = new TcpConnection("localhost", 9876)) {
            connection.connect();
            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.close();
    }

    @Test
    public void testDefaultPort() throws Exception {
        Properties properties = new Properties();

        TcpServer instance = new TcpServer(properties);
        instance.runServer();

        try (TcpConnection connection = new TcpConnection("localhost", Integer.valueOf(Consts.TCP_DEFAULT_PORT))) {
            connection.connect();
            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.close();
    }

    @Test
    public void simpleCommand() throws Exception {
        TcpServer instance = new TcpServer();
        instance.runServer();

        try (TcpConnection connection = new TcpConnection("localhost", 6543)) {
            connection.connect();
            connection.executeCommand(new Command(Command.CommandType.GET, "key123"));
        }

        instance.close();
    }

    @Test
    public void testCommand() throws Exception {
        TcpServer instance = new TcpServer();
        instance.runServer();

        try (TcpConnection connection = new TcpConnection("localhost", 6543)) {
            connection.connect();

            Response response = connection.executeCommand(new Command(Command.CommandType.PUT, "key123", "val123"));
            assertEquals(0, response.getStatus());

            response = connection.executeCommand(new Command(Command.CommandType.GET, "key123"));
            assertEquals("val123", response.getValue());

            response = connection.executeCommand(new Command(Command.CommandType.DELETE, "key123"));
            assertEquals(0, response.getStatus());

            response = connection.executeCommand(new Command(Command.CommandType.GET, "key123"));
            assertNull(response.getValue());

            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.close();
    }

    @Test(expected = BindException.class)
    public void shouldRunOnlyOneInstance() throws Exception {
        try (TcpServer instance1 = new TcpServer(); TcpServer instance2 = new TcpServer()) {
            instance1.runServer();
            instance2.runServer();
        }
    }

    @Test
    public void loadTest() throws Exception
    {
        TcpServer instance = new TcpServer(6543);
        instance.runServer();
        Thread[] threads = new Thread[10];
        for (int i=0; i<threads.length;i++) {
            threads[i] = new Thread(new Worker());
            threads[i].start();
        }
        for (int i=0; i<threads.length;i++) {
            threads[i].join();
        }
        instance.close();
    }

    private class Worker implements Runnable
    {
        @Override
        public void run()
        {
            for (int i = 0; i < 1; i++)
            {

                    try (TcpConnection connection = new TcpConnection("localhost", 6543))
                    {
                        connection.connect();
                        connection.executeCommand(new Command(Command.CommandType.PUT, "key123", "val123"));
                        Response r = connection.executeCommand(new Command(Command.CommandType.GET, "key123"));
                        System.out.println(r.getValue());
                        assertEquals(0, r.getStatus());
                        assertEquals("val123", r.getValue());
                    } catch (ClassNotFoundException | IOException e)
                    {
                        e.printStackTrace();
                    }

            }
        }
    }
}
