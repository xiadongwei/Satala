package extend;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

public class SortIPAddress {
	
	private IPAndHostPackage [] IPAddress=null;
	
	private Hashtable<Integer,IPAndHostPackage> IPHashtable=new Hashtable<Integer,IPAndHostPackage>();
	
	private Integer[] sortNumber = null;
	
	public SortIPAddress(IPAndHostPackage[] ipAddress){
	
		super();
		
		this.IPAddress = ipAddress;
		
		this.sortNumber = new Integer[ipAddress.length];
		
		for (int i=0;i<this.IPAddress.length;i++){
			int i4,i3,i2,i1;
			i4=new Integer(splitToken(this.IPAddress[i].IPAddress,".").get(0)).intValue()*256*256*256;
			i3=new Integer(splitToken(this.IPAddress[i].IPAddress,".").get(1)).intValue()*256*256;
			i2=new Integer(splitToken(this.IPAddress[i].IPAddress,".").get(2)).intValue()*256;
			i1=new Integer(splitToken(this.IPAddress[i].IPAddress,".").get(3)).intValue();
			this.sortNumber[i]=new Integer(i4+i3+i2+i1);
			IPHashtable.put(this.sortNumber[i],this.IPAddress[i]);
		}
		try {
			QSortAlgorithm qs = new QSortAlgorithm();
			qs.sort(this.sortNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=0;i<this.IPAddress.length;i++){
			this.IPAddress[i]=(IPAndHostPackage) IPHashtable.get(this.sortNumber[i]);
		}
	}

	private List<String> splitToken(String ipAddress,String character) {
		
		StringTokenizer pt = new StringTokenizer(ipAddress, character);
		
		List<String> l = new ArrayList<String>();
		
		while (pt.hasMoreTokens()) {
		
			l.add(pt.nextToken());
		}
		return l;
	}
	public String [] getSortedIPAddress(){
		String [] str = new String[this.IPAddress.length];
		for (int i=0; i<this.IPAddress.length; i++){
			str[i]=this.IPAddress[i].IPAddress+" "+this.IPAddress[i].HostName;
		}
		return str;
	}
}
