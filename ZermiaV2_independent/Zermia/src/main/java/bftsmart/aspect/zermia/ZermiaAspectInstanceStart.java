package bftsmart.aspect.zermia;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//
//import zermia.runtime.ZermiaRuntime;
//
//@Aspect	
public class ZermiaAspectInstanceStart {
//	
//	@Before("execution (* bftsmart.demo.counter.CounterServer.main*(..))")
//	 	public void advice(JoinPoint joinPoint) {
//		String[] ReplicaArgs = (String[]) joinPoint.getArgs()[0];
//		String replicaID = ReplicaArgs[0];
//		System.out.println("ZERMIA RUNTIME STARTING IN REPLICA : " +  replicaID);
//		ZermiaRuntime.getInstance().setID(replicaID);
//        try {
//            ZermiaRuntime.getInstance().InstanceStart(); //runtime start
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//	}
}
