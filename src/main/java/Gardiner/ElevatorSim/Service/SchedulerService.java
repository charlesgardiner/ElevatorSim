package Gardiner.ElevatorSim.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import Gardiner.ElevatorSim.Models.Elevator;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Models.PickUpRequest;

@Service
public class SchedulerService {

	private ArrayList<Elevator> elevators = new ArrayList<>();

	private ArrayList<PickUpRequest> pickUpRequestQueue = new ArrayList<>();

	private static int GLOBAL_CLOCK = 0;
	private static int MAX_FLOOR_LEVEL = 1;
	private static int BATCH_DISTANCE = 1; // distance maintained between elevators
	private static int BATCH_DISTANCE_CLOCK = 0; // clock for how long this batch distance existed

	private static final Comparator<Elevator> COMPARE_ELEVATOR = (elevator1, elevator2) -> Integer
			.compare(elevator1.getCurrentFloor(), elevator2.getCurrentFloor());

	public SchedulerService() {

	}

	public int getElevatorCount() {
		return elevators.size();
	}

	public String getSystemStatus() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("System Status\n");
		strBuilder.append("At Step: " + GLOBAL_CLOCK + "\n\n");
		strBuilder.append("Elevator Count: " + elevators.size() + "\n\n");
		elevators.stream().forEach(e -> strBuilder.append(e.toString()));
		strBuilder.append("\n\n");
		strBuilder.append("Pick Up Request Count: " + pickUpRequestQueue.size());
		pickUpRequestQueue.stream().forEach(pur -> strBuilder.append(pur.toString()));

		return strBuilder.toString();
	}

	public String getElevatorStatus(UUID uid) {
		Optional<Elevator> elevator = elevators.stream().filter(e -> e.getId().equals(uid)).findFirst();
		if (elevator.isPresent()) {
			return elevator.get().toString();
		}
		return "no elevator of id: " + uid + "\n";
	}

	public List<Elevator> createElevators(int quantity) {

		// only create elevators once
		if (elevators.size() == 0) {
			IntStream.range(0, quantity).forEach(number -> elevators.add(new Elevator()));
		}
		return elevators;
	}

	public void processPickUpRequest(PickUpRequest pickupRequest) {
		// multiple request could happen during a step queue them for processing

		// adjust MAX FLOOR LEVEL if needed
		if (pickupRequest.getFromFloor() > MAX_FLOOR_LEVEL) {
			MAX_FLOOR_LEVEL = pickupRequest.getFromFloor();
			BATCH_DISTANCE = MAX_FLOOR_LEVEL / elevators.size();
		}
		if (pickupRequest.getToFloor() > MAX_FLOOR_LEVEL) {
			MAX_FLOOR_LEVEL = pickupRequest.getToFloor();
			BATCH_DISTANCE = MAX_FLOOR_LEVEL / elevators.size();
		}

		pickUpRequestQueue.add(pickupRequest);
	}

	public void proccesStepRequest() {

		GLOBAL_CLOCK++;
		BATCH_DISTANCE_CLOCK++;

		// process any requests that happened running the step
		for (PickUpRequest pickUpRequest : pickUpRequestQueue) {

			if (pickUpRequest.isProcessed()) {
				continue;
			}

			// get elevators that match direction of current request
			Stream<Elevator> directionalElevators = elevators.stream()
					.filter(elevator -> elevator.getDirection() == pickUpRequest.getDirection());

			// get elevators that match direction and current floor equals the pick up floor
			Optional<Elevator> optElevator = directionalElevators
					.filter(elevator -> elevator.getCurrentFloor() == pickUpRequest.getFromFloor()).findFirst();

			if (optElevator.isPresent()) {
				optElevator.get().addPickUpRequest(pickUpRequest);
				pickUpRequest.setProcessed(true);
				continue;
			}

			// if none are on the exact floor,
			// if the request is up, find elevator with current floor closest without going
			// over
			// if the request is down, find elevator with current floor closest without
			// going under
			// recreate the directional Elevator stream
			directionalElevators = elevators.stream()
					.filter(elevator -> elevator.getDirection() == pickUpRequest.getDirection());

			if (pickUpRequest.getDirection() == ElevatorDirection.UP) {
				Optional<Elevator> elevator = directionalElevators
						.filter(e -> e.getCurrentFloor() < pickUpRequest.getFromFloor()).max(COMPARE_ELEVATOR);

				// it is possible that a pick up request is left waiting to be processed until
				// an elevator turns around.
				// this is the weakness of the algorithm
				if (elevator.isPresent()) {
					elevator.get().addPickUpRequest(pickUpRequest);
					pickUpRequest.setProcessed(true);
					continue;
				}
			}

			if (pickUpRequest.getDirection() == ElevatorDirection.DOWN) {

				Optional<Elevator> elevator = directionalElevators
						.filter(e -> e.getCurrentFloor() > pickUpRequest.getFromFloor()).min(COMPARE_ELEVATOR);

				// it is possible that a pick up request is left waiting to be processed until
				// an elevator turns around.
				// this is the weakness of the algorithm
				if (elevator.isPresent()) {
					elevator.get().addPickUpRequest(pickUpRequest);
					pickUpRequest.setProcessed(true);
					continue;
				}
			}
		}

		
		/*
		 * This next section is the main part of my scheduler.
		 * The batch distance is the distance maintained between elevators.
		 * However this is only enforced on the 1st floor. 
		 * (in this simulator the first floor is assumed to be the bottom)
		 * 
		 * If the batching distance is 1, then the scheduler simply moves all 
		 * elevators given enough time.  This would mean if you had 10 elevators
		 * on 10 floors there would be an elevator on every floor at all times, after 10 steps.
		 * 
		 * Once the Batch distance gets more than 1, it is only enforced on floor 1.
		 * The batch distance will increase if a pick-up request has a higher floor than 
		 * previously known.  The Max floor level of an elevator changes when the elevator 
		 * leaves the first floor. ( I know this kind of change the definition of an elevator, 
		 * but the alternative was elevators could be lost to a constantly rising building)
		 * 
		 * 
		 */
		if (BATCH_DISTANCE <= 1) {
			for (int i = 0; i < elevators.size(); i++) {
				if (GLOBAL_CLOCK > i && elevators.get(i).getCurrentFloor() == 1) {
					elevators.get(i).setMaxFloor(MAX_FLOOR_LEVEL);
					elevators.get(i).step();
				} else if (elevators.get(i).getCurrentFloor() != 1) {
					elevators.get(i).step();
				}
			}
		} else {
			// when you get to the bottom floor enforce a new batch distance
			for (int i = 0; i < elevators.size(); i++) {
				if (i > 0 && elevators.get(i).getCurrentFloor() == 1 && (elevators.get(i - 1).getCurrentFloor()
						- elevators.get(i).getCurrentFloor() > BATCH_DISTANCE)) {
					elevators.get(i).setMaxFloor(MAX_FLOOR_LEVEL);
					elevators.get(i).step();
				} else if (i > 0 && elevators.get(i).getCurrentFloor() != 1) {
					elevators.get(i).step();
				}
				if (i == 0) {
					if (elevators.get(i).getCurrentFloor() == 1) {
						elevators.get(i).setMaxFloor(MAX_FLOOR_LEVEL);
					}
					elevators.get(i).step();
				}
			}
		}

		// remove any satisfied elevators
		elevators.stream().forEach(e -> {
			if (!e.getPickUpRequests().isEmpty()
					&& (e.getPickUpRequests().get(0).getToFloor() == e.getCurrentFloor())) {
				System.out.println("Pick up request: " + e.getPickUpRequests().get(0).getId() + "completed");
				e.getPickUpRequests().remove(0);
			}
		});
	}
}
