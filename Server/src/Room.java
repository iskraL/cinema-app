public class Room {
    private int Id;
    private int capacity;
    private boolean isAvailable;

    public Room(int Id, int capacity){
        this.Id = Id;
        this.capacity = capacity;
        this.isAvailable = true;
    }

    public int getId() {
        return Id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setIsAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

}
