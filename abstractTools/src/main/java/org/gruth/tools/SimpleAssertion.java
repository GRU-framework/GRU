package org.gruth.tools;

/**
 * @author bamade
 */
// Date: 10/05/15


import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Many instances of this report element may be gathered
 *  into a more global test report.
 *  Avoid using constructors directly and prefer
 *  the factories  defined in the {@link org.gruth.tools.SingleTestReport} class
 *  (that creates <TT>SimpleAssertion</TT> instances
 *  and register them in a <TT>SingleTestReport</TT> report.
 */
public  class SimpleAssertion implements Serializable {
    /**
     * The required diagnostic. Mandatory field
     */
    private Diag diag;
    /**
     * Optional field (may be null).
     * Programmers should take care that the corresponding
     * object should be <TT>Serializable</TT>
     */
    private Throwable caught ;
    /**
     * Mandatory field (but could be an empty String).
     * This field might be managed a  <TT>MessageFormat</TT>
     * pattern String (and used as a key for
     * a I18N ResourceBundle).
     */
    private transient String messageText ;
    /**
     * Mandatory field (but could be a zero length array).
     * The Objects may be used as argument
     * to the <TT>MessageFormat</TT> pattern.
     * If the current report is serialized this array
     * will be used to format the message and then
     * will be emptied (because some objects may not be
     * <TT>Serializable</TT>
     */
    private transient Object[] args ;

    /**
     * constructor to be used to report a Throwable
     * @param diag
     * @param caught
     * @param messageText
     * @param args
     */
    public SimpleAssertion(Diag diag, Throwable caught, String messageText, Object... args) {
        setDiag(diag);
        setCaught(caught);
        setMessageText(messageText);
        setArgs(args);
    }

    /**
     * constructor to be used to report a diagnostic
     * and the corresponding message.
     * @param diag
     * @param messageText
     * @param args
     */
    public SimpleAssertion(Diag diag, String messageText, Object... args) {
        setDiag(diag);
        setMessageText(messageText);
        setArgs(args);
    }

    /**
     *
     * @return the diagnostic (guaranteed not null)
     */
    public Diag getDiag() {
        return diag;
    }

    /**
     * sets the diagnostic
     * @param diag
     * @throws NullPointerException if arg is null
     */
    public void setDiag(Diag diag) {
        this.diag = Objects.requireNonNull(diag, "diagnostic should be set") ;
    }

    /**
     * @return an (optional) Throwable that may have been reported caught
     */
    public Optional<Throwable> getCaught() {
        return Optional.ofNullable(caught);
    }

    /**
     * reports a <TT>Throwable</TT> that may have been caught.
     *
     * @param caught can be null
     */
    public void setCaught(Throwable caught) {
        this.caught = caught;
    }

    /**
     * @return the core message (<TT>MessageFormat</TT> format)
     * guaranteed not null (but can be empty)
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * sets the messageFormat
     * @param messageText
     * @throws NullPointerException if argument is null
     */
    public void setMessageText(String messageText) {
        this.messageText = Objects.requireNonNull(messageText,"message for assertion is mandatory");
    }

    /**
     * @return an array of Object as argument to the message text
     * format. Guaranteed not null
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * sets the argument for formatting the message
     * @param args
     * @throws NullPointerException if null
     */
    public void setArgs(Object[] args) {
        this.args =Objects.requireNonNull(args, "arguments to messages may be empty but not null") ;
    }

    /**
     * Serialization utility
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        String realMessage = PackCommons.formatMessage(messageText, args) ;
        out.writeUTF(realMessage);
    }

    /**
     * Seiralization utility
     * @param input
     * @throws IOException , ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject() ;
        this.messageText = input.readUTF();
        this.args =  new Object[0] ;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SimpleAssertion{");
        sb.append("diag=").append(diag);
        if(caught != null) {
            sb.append(", caught=").append(caught);
        }
        if(messageText != null) {
            sb.append(", message = " + PackCommons.formatMessage(messageText, args)) ;
        }
        sb.append('}');
        return sb.toString();
    }
}

