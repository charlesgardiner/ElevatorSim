package Gardiner.ElevatorSim.Models;

import java.util.UUID;

public class PickUpRequest {

	private UUID id;
	private int fromFloor;
	private int toFloor;
	private ElevatorDirection direction;
	private boolean processed = false;

	public PickUpRequest(int fromFloor, int toFloor, ElevatorDirection direction) {
		super();
		this.id = UUID.randomUUID();
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;
		this.direction = direction;
	}

	public UUID getId() {
		return id;
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

	public String toString() {
		String status = "Pick Up Request: " + id + ": " + "from floor " + fromFloor + " " + direction + " to " + toFloor
				+ "\n";
		return status;
	}

}
