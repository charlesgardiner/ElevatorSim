package Gardiner.ElevatorSim.Commands;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import Gardiner.ElevatorSim.Models.PickUpRequest;
import Gardiner.ElevatorSim.Models.Elevator;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Service.SchedulerService;

/***
 * Class for all the Elevator commands
 * 
 * @author charlesgardiner
 *
 */
@ShellComponent
public class ElevatorSimCommands {

	@Autowired
	private SchedulerService schedulerService;

	/**
	 * Step the Elevator system
	 * 
	 * @param count
	 *            number of steps to take
	 * @return finished string
	 */
	@ShellMethod("Step the System")
	public String Step(@ShellOption(defaultValue = "1") int count) {
		if (schedulerService.getElevatorCount() == 0) {
			return "Please create elevators first";
		}
		for (int i = 0; i < count; i++) {
			System.out.println("Processing Step . . .");
			schedulerService.proccesStepRequest();
		}
		return "All done.";
	}

	/**
	 * Pick up request
	 * 
	 * @param fromFloor
	 *            pick up passengrs from this floor
	 * @param direction
	 *            the direction of the request
	 * @param destinationFloor
	 *            the destination floor
	 * @return string with id of request
	 */
	@ShellMethod("Pick up an elevator")
	public String PickUp(int fromFloor, @Pattern(regexp = "(DOWN|UP|down|up)") String direction, int destinationFloor) {

		ElevatorDirection rd = ElevatorDirection.valueOf(direction.toUpperCase());
		PickUpRequest pur = new PickUpRequest(fromFloor, destinationFloor, rd);
		schedulerService.processPickUpRequest(pur);
		return "Pick up request " + pur.getId() + " created";
	}

	/**
	 * Create the elevators
	 * 
	 * @param count
	 *            the number of elevators to create
	 * @return status of the elevators
	 */
	@ShellMethod("Create the elevators")
	public String Elevators(@Max(16) @Min(1) int count) {
		List<Elevator> elevators = schedulerService.createElevators(count);
		StringBuilder elevatorStrBuilder = new StringBuilder();
		elevators.stream().forEach(e -> elevatorStrBuilder.append(e.toString()));
		return elevatorStrBuilder.toString();
	}

	/**
	 * Gets the system status
	 * 
	 * @return the system status
	 */
	@ShellMethod("Get System Status")
	public String GetSystemStatus() {
		String status = schedulerService.getSystemStatus();
		return status;
	}

	/**
	 * Get the status of the elevator
	 * 
	 * @param uid
	 *            id of the elevator
	 * @return the status of the elevator
	 */
	@ShellMethod("Get Status of specific elevator")
	public String GetElevatorStatus(UUID uid) {
		return schedulerService.getElevatorStatus(uid);
	}

}
