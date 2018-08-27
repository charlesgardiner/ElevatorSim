package Gardiner.ElevatorSim.Models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Elevator {

	private UUID id;
	private ArrayList<PickUpRequest> pickUpRequests = new ArrayList<>();
	private int currentFloor;
	private ElevatorDirection direction;
	private int maxFloor;
	
	private static final Comparator<PickUpRequest> COMPARE_PICKUPREQUEST = (pur1, pur2) -> Integer.compare(pur1.getToFloor(), pur2.getToFloor());

	
	public Elevator() {
		id = UUID.randomUUID();
		currentFloor = 1;
		direction = ElevatorDirection.UP;	
		maxFloor = 1;
	}
	
	public UUID getId() {
		return id;
	}
	
	public void setMaxFloor(int floor) {
		this.maxFloor = floor;
	}

	public List<PickUpRequest> getPickUpRequests() {
		return pickUpRequests;
	}

	public void addPickUpRequest(PickUpRequest pickUpRequest) {
		pickUpRequests.add(pickUpRequest);
		
		// filter out liars (if a down request was really for up, vice versa)
		Stream<PickUpRequest> honestPUR = pickUpRequests.stream().filter(pur -> pur.getDirection() == this.direction)
					.sorted( this.direction == ElevatorDirection.UP ? COMPARE_PICKUPREQUEST : COMPARE_PICKUPREQUEST.reversed() );
		
		Stream<PickUpRequest> liarPUR = pickUpRequests.stream().filter(pur -> pur.getDirection() != this.direction);

		// append liars to end of stream
		this.pickUpRequests = (ArrayList<PickUpRequest>) Stream.concat(honestPUR, liarPUR).collect(Collectors.toList());
	}
	

	public void step() {
		
		if (direction == ElevatorDirection.UP && currentFloor < maxFloor) {
			currentFloor++;
			// need to swtich directions?
			if (currentFloor == maxFloor) {
				direction = ElevatorDirection.DOWN;
			}
		} else if (direction == ElevatorDirection.DOWN && currentFloor > 1) {
			currentFloor--;
			// need to switch directions?
			if (currentFloor == 1) {
				direction = ElevatorDirection.UP;
			}
		}
	}
	
	public ElevatorDirection getDirection() {
		return direction;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}
	
	public String toString() {
		String elevator = "Elevator " + id.toString() + " is on floor " + currentFloor + "\n";
		elevator += "  It is headed " + direction + "\n";
		if (pickUpRequests.size() == 0) {
			elevator += "  It has no pick up requests\n";
			return elevator;
		}
		StringBuilder purs = new StringBuilder();
		pickUpRequests.stream().forEach(pur -> purs.append("  " + pur.toString()));
		return elevator + "It has Pick Up Requests \n" + purs.toString();
	}
	
}
