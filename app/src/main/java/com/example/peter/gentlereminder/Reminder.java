package com.example.peter.gentlereminder;

public class Reminder {
    // instance vars
    private String title;
    private String note;
    private int id;
    private boolean deleted;

    /**
     * Empty constructor for the reminder class
     */
    public Reminder()
    {
        // empty constructor
    }

    /**
     * Get the deleted status
     *
     * @return  if the reminder is deleted
     */
    public boolean isDeleted()
    {
        return deleted;
    }

    /**
     * Set if the reminder is deleted
     *
     * @param deleted   if the reminder is deleted
     */
    public void setDeleted(boolean deleted)
    {
        this.deleted = deleted;
    }

    /**
     * Gets the id
     *
     * @return  the id number for the reminder object
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id    the id number for the reminder object
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Gets the title
     *
     * @return  title of the reminder
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title
     *
     * @param title the title to set for the reminder
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the note
     *
     * @return  note of the reminder
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note
     *
     * @param note  the note to set for the reminder
     */
    public void setNote(String note) {
        this.note = note;
    }

}
