package Gardiner.ElevatorSim.Commands;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.omg.PortableInterceptor.RequestInfoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import Gardiner.ElevatorSim.Models.PickUpRequest;
import Gardiner.ElevatorSim.Models.ElevatorDirection;
import Gardiner.ElevatorSim.Service.SchedulerService;

@ShellComponent
public class ElevatorSimCommands {

	@Autowired
	private SchedulerService schedulerService;
	
	@ShellMethod("Step")
	public String Step() {
		if (schedulerService.getElevatorCount() == 0) {
			return "Please create elevators first";
		}
		schedulerService.proccesStepRequest();
		return "Processed Step";
	}
	
	@ShellMethod("PickUp")
	public String PickUp(int fromFloor, @Pattern(regexp = "(DOWN|UP|down|up)") String direction, int destinationFloor) {
	
		ElevatorDirection rd = ElevatorDirection.valueOf(direction.toUpperCase());
		PickUpRequest pur = new PickUpRequest(fromFloor, destinationFloor, rd);
		schedulerService.processPickUpRequest(pur);
		return "Pick up request received";
	}
	
	@ShellMethod("Set Elevators")
	public String Elevators(@Max(16) @Min(1)int count) {
		boolean success = schedulerService.createElevators(count);
		if (success) {
			return "creating " + count + " elevators";
		}
		return "elevators already exist";
		
	}
	
}
