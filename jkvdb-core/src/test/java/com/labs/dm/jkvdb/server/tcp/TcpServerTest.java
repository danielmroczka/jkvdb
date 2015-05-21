package com.labs.dm.jkvdb.server.tcp;

import com.labs.dm.jkvdb.Consts;
import org.junit.*;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author daniel
 */
public class TcpServerTest {
    
    public TcpServerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRunServer() throws Exception {
        TcpServer instance = new TcpServer();
        instance.runServer();
        
        try (TcpConnection connection = new TcpConnection("localhost", 6543)) {
            connection.connect();
            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.stopServer();
    }
    
    @Test
    public void testLoadFromProeprties() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("tcp.port", "9876");
        TcpServer instance = new TcpServer(properties);
        instance.runServer();
        
        try (TcpConnection connection = new TcpConnection("localhost", 9876)) {
            connection.connect();
            assertTrue(connection.isConnected());
        }
        TimeUnit.MILLISECONDS.sleep(10);
        instance.stopServer();
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
        instance.stopServer();
    }     
}