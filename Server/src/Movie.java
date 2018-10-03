import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Movie {
    private String name;
    private int seatsCount;
    private Set<Integer> seatsTaken;
    private int id;

    public Movie(int id, String name, int seatsCount) {
        setId(id);
        setName(name);
        setSeatsCount(seatsCount);
        seatsTaken = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeatsCount() {
        return seatsCount;
    }

    public void setSeatsCount(int seatsCount) {
        this.seatsCount = seatsCount;
    }

    public void takeSeat(int seatNumber) throws Exception {
        if(seatNumber >= getSeatsCount()) {
            throw new Exception("Invalid seat number");
        }

        if(this.isSeatTaken(seatNumber)) {
            throw new Exception("Seat is taken");
        }

        this.seatsTaken.add(seatNumber);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeatTaken(int seatNumber) {
        return this.seatsTaken.contains(seatNumber);
    }

    public List<Integer> getFreeSeats() {
        return IntStream.range(0, seatsCount)
                .filter(seat -> !seatsTaken.contains(seat))
                .boxed()
                .collect(Collectors.toList());
    }
}
