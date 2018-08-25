package Gardiner.ElevatorSim.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import Gardiner.ElevatorSim.Models.Elevator;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Models.PickUpRequest;

@Service
public class SchedulerService {

	private ArrayList<Elevator> elevators = new ArrayList<>();
	
	private ArrayList<PickUpRequest>pickUpRequestQueue = new ArrayList<>();
	
	private static int GLOBAL_CLOCK = 0;
	private static int MAX_FLOOR_LEVEL = 1;
	private static int BATCH_DISTANCE = 1; // distance maintained between elevators
	private static int BATCH_DISTANCE_CLOCK = 0; // clock for how long this batch distance existed
	
	
	private static final Comparator<Elevator> COMPARE_ELEVATOR = (elevator1, elevator2) -> Integer.compare(elevator1.getCurrentFloor(), elevator2.getCurrentFloor());
	
	public SchedulerService() {
		
	}
	
	public int getElevatorCount() {
		return elevators.size();
	}
	
	public boolean createElevators(int quantity) {
		
		// only create elevators once
		if (elevators.size() == 0) {
			IntStream.range(0, quantity).forEach(number ->  elevators.add(new Elevator()));
			return true;
		}
		return false;
	}
	
	// passing pur guid?
	public void processPickUpRequest(PickUpRequest pickupRequest) {
		// multiple request could happen during a step queue them for processing
		
		// adjust MAX FLOOR LEVEL if needed
		if (pickupRequest.getFromFloor() > MAX_FLOOR_LEVEL) {
			MAX_FLOOR_LEVEL = pickupRequest.getFromFloor();
		}
		if (pickupRequest.getToFloor() > MAX_FLOOR_LEVEL) {
			MAX_FLOOR_LEVEL = pickupRequest.getToFloor();
		}
		
		int new_batch_distance = MAX_FLOOR_LEVEL / elevators.size();
		if (new_batch_distance != BATCH_DISTANCE) {
			BATCH_DISTANCE = new_batch_distance;
			BATCH_DISTANCE_CLOCK = 0;
		}
		
		pickUpRequestQueue.add(pickupRequest);
	}
	
	public void proccesStepRequest() {
		
		GLOBAL_CLOCK++;
		BATCH_DISTANCE_CLOCK++;
		
		// process any requests that happened running the step	
		for (PickUpRequest pickUpRequest : pickUpRequestQueue) {
			
			if(pickUpRequest.isProcessed()) {
				continue;
			}
			
			// get elevators that match direction of current request
			Stream<Elevator> directionalElevators = elevators.stream()
					.filter(elevator -> elevator.getDirection() == pickUpRequest.getDirection());
			
			// get elevators that match direction and current floor equals the pick up floor
			Optional<Elevator>  optElevator =  directionalElevators
					.filter(elevator -> elevator.getCurrentFloor() == pickUpRequest.getFromFloor()).findFirst();
			
			if(optElevator.isPresent()) {
				optElevator.get().addPickUpRequest(pickUpRequest);
				pickUpRequest.setProcessed(true);
				continue;
			}
			
			// if none are on the exact floor,
			// if the request is up,	find elevator with current floor closest without going over
			// if the request is down, find elevator with current floor closest without going under
			// recreate the directional Elevator stream
			directionalElevators = elevators.stream()
					.filter(elevator -> elevator.getDirection() == pickUpRequest.getDirection());
			
			if (pickUpRequest.getDirection() == ElevatorDirection.UP) {
				Optional<Elevator> elevator = directionalElevators
					.filter(e -> e.getCurrentFloor() < pickUpRequest.getFromFloor())
					.max(COMPARE_ELEVATOR);
				
				// it is possible that a pick up request is left waiting to be processed until an elevator turns around.
				// this is the weakness of the algorithm
				if (elevator.isPresent()) {
					elevator.get().addPickUpRequest(pickUpRequest);
					pickUpRequest.setProcessed(true);
					continue;
				}
			}
			
			if (pickUpRequest.getDirection() == ElevatorDirection.DOWN) {
				
				Optional<Elevator> elevator = directionalElevators
					.filter(e -> e.getCurrentFloor() > pickUpRequest.getFromFloor())
					.min(COMPARE_ELEVATOR);
				
				// it is possible that a pick up request is left waiting to be processed until an elevator turns around.
				// this is the weakness of the algorithm
				if (elevator.isPresent()) {
					elevator.get().addPickUpRequest(pickUpRequest);
					pickUpRequest.setProcessed(true);
					continue;
				}
			}
		}
		
		// move the elevators
		// to maintain the batch size I need the index
		for(int i = 0; i<elevators.size(); i++) {
			if( BATCH_DISTANCE_CLOCK >= (i+1)*BATCH_DISTANCE) {
				elevators.get(i).step(MAX_FLOOR_LEVEL);
			}
		}
		
		// remove any satisfied elevators
		elevators.stream().forEach(e -> {if (!e.getPickUpRequests().isEmpty() && (e.getPickUpRequests().get(0).getToFloor() == e.getCurrentFloor()))
					e.getPickUpRequests().remove(0);
				});
	}
}
