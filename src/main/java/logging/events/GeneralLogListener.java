package logging.events;

import java.util.logging.Logger;

public class GeneralLogListener implements IEventListener
{
    Logger logger = Logger.getLogger(ConcurrentTransactionListener.class.getName());

    @Override
    public void recordEvent()
    {

    }

    public void generalLog(String time, String record) {
        logger.info("Time taken for query : " +time +"\n" +"records checked:" +record);
    }
}
