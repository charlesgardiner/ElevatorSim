# ElevatorSim
This is an elevator simulator. 

### Techincal Requirements
Java version 9

Maven version 3.5.4

#### How to build

1) clone this repository to a directory
2) in the ElevatorSim directory run ''mvn clean package''
3) run ''java -jar target/ElevatorSim-0.0.1-SNAPSHOT.jar''

### How to Use

List of Commands
- help 

    list the available commands
    
    help followed by any of the command below will give more information
    
- get-system-status

    get the status of the elevator system as whole
    
- elevators [number]

    creates <number> or elevators and returns there status when called
    
    each elevator is assigned a UID upon creation
    
    can only be called once, if called more than once, the status of the initial elevators is returned
   
- pick-up [fromfloor] [DIRECTION (UP or DOWN)] [toFloor]

    enter a pick up request 
    
    [fromFlor] - floor request originates on
    
    [Direction] - to go up or down
    
    [toFloor] - floor request will leave elavator
    
    This is a request to have an elevator pick a person up.  It is meant to simulate a person pressing the arrow button on a floor and then pressing the floor button upon entering the elevator.
    
- step

    moves the system forward one "time tick", effectively each elevator will move forward one floor
    upon this command, all queued pick up requests are processed and then the elevators are moved a floor
    
- get-elevator-status [uuid]

    returns the status of the elevator for the given uuid
    
###  Algorithm Discussion

#### Assumptions made
I assumed that the bottom floor is 1 and the top floor is unkown.  This means the system will need to adjust for an ever
changing building height.  I assumed the users could be dishonest.  A person could press the up arrow on floor 3 of a building then
request to go to the first floor upon entering the elevator.

#### Goal

The goal when designing this system is to minimize the wait time of requests on average over time.

#### A First Come First Serve Elevator

First come first serve elevator systems are a system of 'n' elevators.  When a request is entered into the system, it is
handled by a free elevator or the request waits until a free elevator is available.  The evelvator will only carry one request 
at a time.  One elevator will pick and drop off a request before any more requests are handled.  A first come first serve elevator system has drawbacks.

##### Drawbacks:

- When the number of requests out number, the number elevators, then the requests will start to queue and wait times 
will increase.

- For a large building, a small number of large requests (from the first floor up to the top floor), will also increase the 
wait time of requests.

- If a building has a busy area (floors that make a large number of requests), the first come first serve algorithm will 
unfairly punish requests that are far from the busy area.

- Over time the average wait time of any request would be an amount of steps equal to half the height of the building.  Some requests will be very close to the assigned elevator, but some will require an elevator to cross the entire building.  The first come first serve does not take advantage of excess elevators, as requests can come from any floor.

#### Solution: The Always Moving and On My Way approach

Elevators in this approach can handle multiple requests, pick up more than one request at a time.  The Elevators will constantly move, even when empty. They will space themselves out to maximize coverage of the building.

The algorithm works by "peeking" at pick up requests to determine the maximum height of the build.  When the first request is entered, the maximum height of the build is determined and a "batching distance" is calculated.  Then the first elevator starts moving upwards toward the top the of the building.  The second elevator starts moving after the first elevator is a "batching distance" of floors away from the second elevator.  Then the second elevator starts moving.  The 3rd elevator starts moving when the 2nd elevator is a "batching distance" of floors away from the 3rd elevator.  This pattern continues for all elevatotrs.

The "batching distance" is calculated by taken maximum number of floors and dividing it by the number of elevators.  This will ensure that all elevators are evenly spaced throughout the building at all times. There is a special case for when the batching distance is one.



#### Possible oversights

- My elevators do not have a capacity limit.  This is clearly not realistic.
- It is not practical to have elevators to always be moving in the real world as moving an elvator has a cost.
- 
