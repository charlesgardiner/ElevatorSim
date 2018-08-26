package Gardiner.ElevatorSim.Commands;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.omg.PortableInterceptor.RequestInfoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import Gardiner.ElevatorSim.Models.PickUpRequest;
import Gardiner.ElevatorSim.Models.Elevator;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Service.SchedulerService;

@ShellComponent
public class ElevatorSimCommands {

	@Autowired
	private SchedulerService schedulerService;
	
	@ShellMethod("Step the System")
	public String Step() {
		if (schedulerService.getElevatorCount() == 0) {
			return "Please create elevators first";
		}
		schedulerService.proccesStepRequest();
		return "Processed Step";
	}
	
	@ShellMethod("Pick up an elevator")
	public String PickUp(int fromFloor, @Pattern(regexp = "(DOWN|UP|down|up)") String direction, int destinationFloor) {
	
		ElevatorDirection rd = ElevatorDirection.valueOf(direction.toUpperCase());
		PickUpRequest pur = new PickUpRequest(fromFloor, destinationFloor, rd);
		schedulerService.processPickUpRequest(pur);
		return "Pick up request received";
	}
	
	@ShellMethod("Create the elevators")
	public String Elevators(@Max(16) @Min(1)int count) {
		List<Elevator> elevators = schedulerService.createElevators(count);
		StringBuilder elevatorStrBuilder = new StringBuilder();
		elevators.stream().forEach(e -> elevatorStrBuilder.append(e.toString()));
		return elevatorStrBuilder.toString();
	}
	
	@ShellMethod("Get System Status")
	public String GetSystemStatus() {
		String status = schedulerService.getSystemStatus();
		return status;
	}
	
	@ShellMethod("Get Status of specific elevator")
	public String GetElevatorStatus(UUID uid) {
		return schedulerService.getElevatorStatus(uid);
	}
	
}
