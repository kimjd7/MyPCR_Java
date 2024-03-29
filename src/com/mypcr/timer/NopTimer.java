package com.mypcr.timer;

import java.io.IOException;
import java.util.TimerTask;

import com.mypcr.beans.RxAction;
import com.mypcr.function.Functions;
import com.mypcr.ui.MainUI;

public class NopTimer extends TimerTask
{
	public static final int TIMER_DURATION = 100;
	public static final int TIMER_NUMBER   = 0x00;

	private MainUI 	m_MainUI = null;

	public NopTimer(MainUI mainUI)
	{
		m_MainUI = mainUI;
	}
	
	private void logProcess(RxAction rx){
		String message = String.format("State: %d, CurrentAction: %d, TotalAction: %d, TotalLeftTime: %d, LeftTime: %.0f,"
									+  "LeftGoto: %d, TempChamberH: %d, TempChamberL: %d Error: %d", 
									rx.getState(), rx.getCurrent_Action(), rx.getTotal_Action(), rx.getTotal_TimeLeft(), 
									rx.getSec_TimeLeft(), rx.getCurrent_Loop(), rx.getChamber_TempH(), rx.getChamber_TempL(), 
									rx.getError());
		Functions.log(message);
	}

	@Override
	public void run()
	{
		try
		{
			if( m_MainUI.getDevice() != null )
			{
				byte[] readBuffer = new byte[65];

				if( m_MainUI.getDevice().read(readBuffer) != 0 )
				{
					m_MainUI.getPCR_Task().m_RxAction.set_Info(readBuffer);

					m_MainUI.getPCR_Task().Calc_Temp();

					m_MainUI.getPCR_Task().Check_Status();

					m_MainUI.getPCR_Task().Line_Task();

					m_MainUI.getPCR_Task().Get_DeviceProtocol();

					m_MainUI.getPCR_Task().Error_Check();

					m_MainUI.getPCR_Task().Calc_Time();
					
					logProcess(m_MainUI.getPCR_Task().m_RxAction);
				}
				
				m_MainUI.getDevice().write( m_MainUI.getPCR_Task().m_TxAction.Tx_NOP() );
			}
		}catch(IOException e)
		{
			// e.printStackTrace();
		}
	}
}
