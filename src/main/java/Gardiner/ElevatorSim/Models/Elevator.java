package Gardiner.ElevatorSim.Models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Elevator {

	private UUID id;
	//private Stream<PickUpRequest> pickUpRequests = Stream.of();
	private ArrayList<PickUpRequest> pickUpRequests = new ArrayList<>();
	private ArrayList<Long> destinationFloors = new ArrayList<>();
	private int currentFloor;
	private ElevatorDirection direction;
	
	private static final Comparator<PickUpRequest> COMPARE_PICKUPREQUEST = (pur1, pur2) -> Integer.compare(pur1.getToFloor(), pur2.getToFloor());

	
	public Elevator() {
		id = UUID.randomUUID();
		currentFloor = 1;
		direction = ElevatorDirection.UP;		
	}
	
	public UUID getId() {
		return id;
	}

	public List<Long> getDestinationFloors() {
		return destinationFloors;
	}

	public List<PickUpRequest> getPickUpRequests() {
		return pickUpRequests;
	}
	
	public void setDestinationFloors(ArrayList<Long> destinationFloors) {
		this.destinationFloors = destinationFloors;
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
	
	public int getCurrentFloor() {
		return currentFloor;
	}


	// refator?
	public void step(int maxFloor) {
		
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

	
	public String toString() {
		return String.format("Elevator {0}: on floor {1} heading to {2}",
				id.toString(),
				currentFloor,
				destinationFloors.isEmpty() ? "no where" : destinationFloors.get(0));
	}
	
}
