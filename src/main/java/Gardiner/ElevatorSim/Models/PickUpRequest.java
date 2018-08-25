package Gardiner.ElevatorSim.Models;

public class PickUpRequest {

	private int fromFloor;
	private int toFloor;
	private ElevatorDirection direction;
	private boolean processed = false;
	
	
	
	
	public PickUpRequest(int fromFloor, int toFloor, ElevatorDirection direction) {
		super();
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;
		this.direction = direction;
	}
	public int getFromFloor() {
		return fromFloor;
	}
	public void setFromFloor(int fromFloor) {
		this.fromFloor = fromFloor;
	}
	public int getToFloor() {
		return toFloor;
	}
	public void setToFloor(int toFloor) {
		this.toFloor = toFloor;
	}
	public ElevatorDirection getDirection() {
		return direction;
	}
	public void setDirection(ElevatorDirection direction) {
		this.direction = direction;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	
	
	
}
