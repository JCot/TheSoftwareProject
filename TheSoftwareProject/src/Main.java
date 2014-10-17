/**
 * 
 * @author Shannon
 *
 */
public class Main {
	private final int day = 4800; //milliseconds
	private final int minute = 10; //milliseconds

	public static void main(String[] args) {
		Manager bob = new Manager("Bob");
		
		//Developer NM, where N is the team number (1-3)
		//and M is the employee's number on the team (1-4,
		//where 1 is the team lead)
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j<=4; j++) {
				Employee e = new Employee("Developer " + i + j, i);
				bob.addEmployee(e);
			}
		}
		
		
		

	}

}
