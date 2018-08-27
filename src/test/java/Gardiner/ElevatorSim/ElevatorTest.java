package Gardiner.ElevatorSim;

import java.util.stream.IntStream;

import Gardiner.ElevatorSim.Models.Elevator;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Models.PickUpRequest;
import junit.framework.TestCase;

public class ElevatorTest extends TestCase {

	public void testElevatorStep() {

		Elevator elevator = new Elevator();

		elevator.setMaxFloor(5);
		elevator.step();
		assertEquals(2, elevator.getCurrentFloor());
		elevator.step();
		elevator.step();
		elevator.step();
		elevator.step();
		assertEquals(ElevatorDirection.DOWN, elevator.getDirection());
	}

	public void testElevatorStepWith1Floor() {

		// Test elevator does not move when floor is only 1
		Elevator elevator = new Elevator();
		elevator.step();
		assertEquals(1, elevator.getCurrentFloor());
	}

	public void testElevatorPickUps() {
		Elevator elevator = new Elevator();
		PickUpRequest pickup1 = new PickUpRequest(3, 5, ElevatorDirection.UP);
		PickUpRequest pickup2 = new PickUpRequest(2, 3, ElevatorDirection.UP);
		PickUpRequest pickup3 = new PickUpRequest(1, 2, ElevatorDirection.UP);

		elevator.addPickUpRequest(pickup1);
		elevator.addPickUpRequest(pickup2);
		elevator.addPickUpRequest(pickup3);

		assertEquals(2, elevator.getPickUpRequests().get(0).getToFloor());
		assertEquals(3, elevator.getPickUpRequests().get(1).getToFloor());

		assertEquals(5, elevator.getPickUpRequests().get(2).getToFloor());
	}

	public void testDownElevatorPickUps() {
		Elevator elevator = new Elevator();
		PickUpRequest pickup1 = new PickUpRequest(5, 3, ElevatorDirection.DOWN);
		PickUpRequest pickup2 = new PickUpRequest(13, 12, ElevatorDirection.DOWN);
		PickUpRequest pickup3 = new PickUpRequest(2, 1, ElevatorDirection.DOWN);

		elevator.setMaxFloor(15);
		IntStream.range(0, 15).forEach(e -> elevator.step());

		elevator.addPickUpRequest(pickup1);
		elevator.addPickUpRequest(pickup2);
		elevator.addPickUpRequest(pickup3);

		assertEquals(12, elevator.getPickUpRequests().get(0).getToFloor());
		assertEquals(3, elevator.getPickUpRequests().get(1).getToFloor());

		assertEquals(1, elevator.getPickUpRequests().get(2).getToFloor());
	}


	 
}
