package pt.tecnico.rec;

import com.google.protobuf.*;
import org.junit.jupiter.api.*;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.Rec.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import static org.junit.jupiter.api.Assertions.*;

public class RecordIT extends BaseIT{
	
	// static members
	// TODO	
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() throws ZKNamingException {

	}
	
	@AfterAll
	public static void oneTimeTearDown() {
		
	}
	
	// initialization and clean-up for each test
	
	@BeforeEach
	public void setUp() {
		
	}
	
	@AfterEach
	public void tearDown() {
		
	}
		
	// tests 
	
	@Test
	public void pingTest() {
		PingRequest pingRequest= PingRequest.newBuilder().build();
		PingResponse pingResponse = frontend.ping(pingRequest);
		assertEquals(pingResponse.getOutputText(), "OK");
	}

	@Test
	public void writeTest() {
		Int32Value message = Int32Value.newBuilder().setValue(0).build();
		Any packedInt = Any.pack(message);
		WriteRequest writeRequest = WriteRequest.newBuilder().setName("test int").
				setValue(packedInt).setVersion(1).build();
		WriteResponse writeResponse = frontend.write(writeRequest);
		assertNotNull(writeResponse);
	}

	@Test
	public void readNullTest() throws InvalidProtocolBufferException {
		ReadRequest request = ReadRequest.newBuilder().setName("test null").build();
		ReadResponse response = frontend.read(request);
		String string = response.getValue().unpack(StringValue.class).getValue();
		assertEquals(string, "-");
		int ver = response.getVersion();
		assertEquals(ver, 0);
	}

	@Test
	public void writeThenReadIntTest() throws InvalidProtocolBufferException {
		Int32Value message = Int32Value.newBuilder().setValue(1).build();
		Any packedInt = Any.pack(message);
		WriteRequest writeRequest = WriteRequest.newBuilder().setName("test read").setValue(packedInt).
				setVersion(2).build();
		WriteResponse writeResponse = frontend.write(writeRequest);
		assertNotNull(writeResponse);

		ReadRequest request = ReadRequest.newBuilder().setName("test read").build();
		ReadResponse response = frontend.read(request);
		int value = response.getValue().unpack(Int32Value.class).getValue();
		assertEquals(1, value);
		int ver = response.getVersion();
		assertEquals(2, ver);
	}

	@Test
	public void writeThenReadBoolTest() throws InvalidProtocolBufferException {
		BoolValue message = BoolValue.newBuilder().setValue(true).build();
		Any packedInt = Any.pack(message);
		WriteRequest writeRequest = WriteRequest.newBuilder().setName("test read 2").setValue(packedInt).
				setVersion(3).build();
		WriteResponse writeResponse = frontend.write(writeRequest);
		assertNotNull(writeResponse);

		ReadRequest request = ReadRequest.newBuilder().setName("test read 2").build();
		ReadResponse response = frontend.read(request);
		boolean value = response.getValue().unpack(BoolValue.class).getValue();
		assertEquals(true, value);
		int ver = response.getVersion();
		assertEquals(3, ver);
	}

}
