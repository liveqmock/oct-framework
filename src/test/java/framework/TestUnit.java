//package framework;
//
//
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.mopon.dao.master.member.IMemberDao;
//import com.mopon.entity.member.Member;
//
//
//
//
//
//@RunWith(SpringJUnit4ClassRunner.class) 
//@ContextConfiguration(locations = {"classpath:application*.xml"})
//public class TestUnit extends AbstractJUnit4SpringContextTests {
//	@Autowired
//	private IMemberDao memberDao;
//
//	@Test
//    public void saveTest() {
//		List<Member> member =  memberDao.queryAll();
//		System.out.println(member);
//    }
//}
