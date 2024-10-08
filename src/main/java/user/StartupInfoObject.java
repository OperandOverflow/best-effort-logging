package user;

import java.util.List;

import actors.Auditor;
import actors.Janitor;
import actors.Reader;
import actors.Writer;

public class StartupInfoObject {
	
	public int numberreads;
	public long objectsize;
	public long readinterval;
	public long[] auditinstants;
	public long[] auditdelay;
	public DataContainer container;
	public List<Writer> writers;
	public List<Reader> readers;
	public List<Auditor> auditors;
	public Janitor janitor;
	public boolean redirectoutput;
	
	public StartupInfoObject() {}
	
	public void setNumberReads(int number) {
		numberreads = number;
	}
	
	public void setObjectSize(long size) {
		objectsize = size;
	}
	
	public void setReadInterval(long interval) {
		readinterval = interval;
	}
	
	public void setAuditInstants(long[] instants) {
		auditinstants = instants;
		auditdelay = new long[auditinstants.length];
		auditdelay[0] = instants.length > 0 ? instants[0] : 0;
		for (int i = 1; i < instants.length; i++)
			auditdelay[i] = auditinstants[i] - auditinstants[i-1];
	}
	
	public void setDataContainer(DataContainer container) {
		this.container = container;
	}
	
	public void setListWriters(List<Writer> list) {
		writers = list;
	}
	
	public void setListReaders(List<Reader> list) {
		readers = list;
	}
	
	public void setListAuditors(List<Auditor> list) {
		auditors = list;
	}
	
	public void setJanitor(Janitor jntr) {
		janitor = jntr;
	}
	
	public void setRedirectOutput(boolean redirection) {
		redirectoutput = redirection;
	}
}
