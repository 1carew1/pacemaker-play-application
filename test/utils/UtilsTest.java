package utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Test;
public class UtilsTest extends EasyMockSupport {
	
	
	@Test
	public void testMysqlConnection(){
		MySQLConnection connection = new MySQLConnection();
		List<Long> ids = connection.getUserIdsFromMysqlQuery("SELECT MAX(ID) AS id FROM pacemaker.user;");
		assertTrue(ids.size() > 0);
	}
	
	@Test
	public void testDBConnectionWithTestStub() {
		String query = "SELECT MAX(ID) AS id FROM pacemaker.user;";
		MySQLConnection con = EasyMock.createMock(MySQLConnection.class);
		List<Long> ids = new ArrayList<>();
		ids.add(10l);
		EasyMock.expect(con.getUserIdsFromMysqlQuery(query)).andReturn(ids);
		EasyMock.replay(con);
		assertTrue(con.getUserIdsFromMysqlQuery(query).size() > 0);
	}
	
	@Test
	public void testMocks() {

//		// Create and train mock
//		List<String> mockedList = mock(List.class);
//		when(mockedList.get(0)).thenReturn("first");
//
//		// check value
//		assertEquals("first", mockedList.get(0));
//
//		// verify interaction
//		verify(mockedList).get(0);
	}
	
}
