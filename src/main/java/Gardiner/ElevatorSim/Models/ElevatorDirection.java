package Gardiner.ElevatorSim.Models;

public enum ElevatorDirection {
	DOWN("DOWN"),
	UP("UP");
	
	final String name;
	
	 ElevatorDirection(String name) {
		this.name = name;
	}
}
