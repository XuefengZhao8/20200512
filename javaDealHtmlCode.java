package supercop_HtmlCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class javaDealHtmlCode {
	public static void supercopTimingData(String dirpath, String pathname,Boolean TORF) {
		//html文件的基本代码		
		String endStr = "</body></html>";
		String startStr = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title></title></head><body><h2>下面包含所以算法的所有版本算法实现速度，其中表格中算法名称大写是该算法所有版本中速度最快的。</h2><h4>例外：ASCON（ascon80pqv12, ascon128v12，ascon128av12），LOTUS-AEAD & LOCUS-AEAD（twegift64locusaeadv1，twegift64lotusaeadv1） 是大写的。</h4>";
//		String startStr = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title></title></head><body>";
			// dirpath表示你所创建文件的路径
		File f = new File(dirpath);
		if (!f.exists()) {
			f.mkdirs();
		}
		//第二轮lwc 分组密码算法的名称，用来正则表达式来  supercop中的算法
		String regex = "^(?i)(.*median.*||.*ACE.*||.*ascon80pqv12.*||.*ascon128v12.*||.*ascon128av12.*||.*COMET.*||.*DryGASCON.*||.*Elephant.*||.*ESTATE.*||.*Fork.*||.*GIFT.*||.*Gimli.*||.*Grain.*||.*HyENA.*||.*ISAP.*||.*KNOT.*||.*LOTUS.*||.*mixFeed.*||.*ORANGE.*||.*Oribatida.*||.*PHOTON.*||.*Pyjamask.*||.*Romulus.*||.*SAEAES.*||.*Saturnin.*||.*SKINNY.*||.*SCHWAEMM.*||.*ESCH.*||.*SPIX.*||.*SpoC.*||.*Spook.*||.*Subterranean.*||.*SUNDAE.*||"
				+ ".*TinyJAMBU.*||.*WAGE.*||.*Xoodyak.*)$";
		// read
		File feileName = new File(pathname);
		int i = 0, k = 20, j;
		//中间变量
		String[] split_result = new String[20];
		String txtname = new String();
		// 35  32个算法进入第二轮 ascon计数为3，多了两个，因为ascon的多个版本（包含凯撒竞赛中的版本）。
		//找出速度最快的12个算法，一个算法的多个变体的速度整体快；用来找到一个算法中的最快的一个变体，即一个算法只记一个最快的
		//AEADname作为临时变量，处理过程中会修改为***。下一次使用，需要用AEADnameTemp赋值给AEADname
		String[] AEADnameTemp = { "ACE", "ascon80pqv12", "ascon128v12", "ascon128av12", "COMET", "DryGASCON", "Elephant",
				"ESTATE", "Fork", "GIFT", "Gimli", "Grain", "HyENA", "ISAP", "KNOT", "LOTUS", "LOCUS", "mixFeed", "ORANGE",
				"Oribatida", "PHOTON", "Pyjamask", "Romulus", "SAEAES", "Saturnin", "SKINNY", "SCHWAEMM",
				"SPIX", "SpoC", "Spook", "Subterranean", "SUNDAE", "TinyJAMBU", "WAGE", "Xoodyak" };
		String[] AEADname = new String[AEADnameTemp.length];
		System.arraycopy(AEADnameTemp,0,AEADname ,0,AEADnameTemp.length);
		int AEADnameLen=AEADnameTemp.length;
		try {
			// read
			InputStreamReader reader = new InputStreamReader(new FileInputStream(feileName));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String lineTemp = "";
			while ((line = br.readLine()) != null) {
				//一个机器的开始标志
				txtname="";
				if (line.matches("^(?i)(.*<h2 id=.*)")) {
					System.out.println(line);
					//除去链接a href="hp://bench.cr.yp.to/web-impl/amd64-r24000-crypto_aead.html">
					lineTemp=line.replaceAll((String)(line.subSequence(line.indexOf("; <a href=\"h"), line.indexOf("html\">")+7)),"");
					//根据id=，;，tt进行分割
					split_result = lineTemp.split("id=|;|tt");
					//连接形成txt名字  。split_result[0]为<h2 ;   split_result[split_result.length-1]为</h2>
					for (j = 1; j < split_result.length-1; j++) {
						txtname = txtname.concat(split_result[j].replaceAll("\\W", "_"));
					}
					System.out.println(txtname);
					// write  新建该目录下的该名称的html文档
					File file = new File(f, txtname + ".html");
					FileOutputStream fileOutputStream = null;
					if (file.exists()) {
						// 判断文件是否存在，如果不存在就新建一个txt
						file.createNewFile();
					}
					fileOutputStream = new FileOutputStream(file);
					//html的基本代码
					fileOutputStream.write(startStr.getBytes());
					//写入<h2 id= 内容
					fileOutputStream.write(line.getBytes());
					fileOutputStream.write("\r\n".getBytes());
					// 一次读入一行数据，判断下一句是否为Cycles/byte，即Cycles/byte的前一句是<table border=\"\">
					while ((line = br.readLine()) != null && !line.matches("<table border=\"\">")) {
							fileOutputStream.write(line.getBytes());
							fileOutputStream.write("\r\n".getBytes());
					}
					//写数据 <table border=\"\">
					fileOutputStream.write(line.getBytes());
					fileOutputStream.write("\r\n".getBytes());
					//</tr></tbody></table>为结束标志
					while ((line = br.readLine()) != null && !line.matches("</tr></tbody></table>")) {
						if (line.matches("^(?i)(.*Cycles/byte.*)")) {
							//写数据 Cycles/byte
								fileOutputStream.write(line.getBytes());
								fileOutputStream.write("\r\n".getBytes());
								AEADnameLen=AEADnameTemp.length;
								//用AEADnameTemp初始化AEADname
								System.arraycopy(AEADnameTemp,0,AEADname ,0,AEADnameTemp.length);
							if (line.matches("^(?i)(.*encrypt.*)")) {
								System.out.println(line);
								//</tbody></table>为Cycles/byte的结束标志
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													//将字体加粗
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
													//字体转换为大写
													line=line.toUpperCase();
													}
												}
											}
										}
										//boolean变量 TORF判断是否写入内容
										if(TORF) {
												fileOutputStream.write(line.getBytes());
												fileOutputStream.write("\r\n".getBytes());											
										}

									}
								}
								/*
								for(i=0;i<AEADname.length;i++ ) {
									if(AEADname[i]!=null)
									System.out.println(i+"  "+AEADname[i]);									
								}
								*/
							}
							if (line.matches("^(?i)(.*decrypt.*)")) {
								System.out.println(line);
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
													line=line.toUpperCase();
													}
												}
											}
										}
										if(TORF) {
											fileOutputStream.write(line.getBytes());
											fileOutputStream.write("\r\n".getBytes());											
									}
									}
								}
							}
							if (line.matches("^(?i)(.*forgery.*)")) {
								System.out.println(line);
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
													line=line.toUpperCase();
													}
												}
											}
										}
										if(TORF) {
											fileOutputStream.write(line.getBytes());
											fileOutputStream.write("\r\n".getBytes());											
									}
									}
								}
								fileOutputStream.write("</tbody></table></td><td><table border=\"\">".getBytes());
								fileOutputStream.write("\r\n".getBytes());
								
							}
						}
					}
					fileOutputStream.write(endStr.getBytes());
					fileOutputStream.flush();
					fileOutputStream.close();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void supercopTimingDataFast(String dirpath, String pathname) {
		//html文件的基本代码		
		String endStr = "</body></html>";
		String startStr = "<html><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /></head><body><p>下面是各个算法速度最快的版本。</p><p>第二轮算法有32个。下面算法实现速度共有35个，其中ascon含3个变体实现（ascon80pqv12, ascon128v12，ascon128av12），LOTUS-AEAD & LOCUS-AEAD 有两个变体（twegift64locusaeadv1，twegift64lotusaeadv1）</p>";
		// dirpath表示你所创建文件的路径
		File f = new File(dirpath);
		if (!f.exists()) {
			f.mkdirs();
		}
		//第二轮lwc 分组密码算法的名称，用来正则表达式来  supercop中的算法
		//String regex = "^(?i)(.*median.*||.*ACE.*||.*ascon80pqv12.*||.*ascon128v12.*||.*ascon128av12.*||.*COMET.*||.*DryGASCON.*||.*Elephant.*||.*ESTATE.*||.*Fork.*||.*GIFTCOFB.*||.*Gimli.*||.*Grain.*||.*HyENA.*||.*ISAP.*||.*KNOT.*||.*LOTUS.*||.*LOCUS.*||.*mixFeed.*||.*ORANGE.*||.*Oribatida.*||.*PHOTONBeetle.*||.*Pyjamask.*||.*Romulus.*||.*SAEAES.*||.*Saturnin.*||.*SKINNY.*||.*SCHWAEMM.*||.*SPIX.*||.*SpoC.*||.*Spook.*||.*Subterranean.*||.*SUNDAEGIFT.*||.*TinyJAMBU.*||.*WAGE.*||.*Xoodyak.*)$";
		String regex = "^(?i)(.*median.*||.*ACE.*||.*ascon80pqv12.*||.*ascon128v12.*||.*ascon128av12.*||.*COMET.*||.*DryGASCON.*||.*Elephant.*||.*ESTATE.*||.*Fork.*||.*GIFTCOFB.*||.*Gimli.*||.*Grain.*||.*HyENA.*||.*ISAP.*||.*KNOT.*||.*LOTUS.*||.*LOCUS.*||.*mixFeed.*||.*ORANGE.*||.*Oribatida.*||.*PHOTONBeetle.*||.*Pyjamask.*||.*Romulus.*||.*SAEAES.*||.*Saturnin.*||.*SKINNY.*||.*SCHWAEMM.*||.*SPIX.*||.*SpoC.*||.*Spook.*||.*Subterranean.*||.*SUNDAEGIFT.*||.*TinyJAMBU.*||.*WAGE.*||.*Xoodyak.*)$";
		// read
		File feileName = new File(pathname);
		int i = 0, k = 20, j;
		//中间变量
		String[] split_result = new String[20];
		String txtname = new String();
		// AEADnameTemp.length=35  32个算法进入第二轮 ascon计数为3，多了两个，因为ascon的多个版本（包含凯撒竞赛中的版本）。LOTUS-AEAD & LOCUS-AEAD有两个
		//找出速度最快的12个算法，一个算法的多个变体的速度整体快；用来找到一个算法中的最快的一个变体，即一个算法只记一个最快的
		//AEADname作为临时变量，处理过程中会修改为***。下一次使用，需要用AEADnameTemp赋值给AEADname
		String[] AEADnameTemp = { "ACE", "ascon80pqv12", "ascon128v12", "ascon128av12", "COMET", "DryGASCON", "Elephant",
				"ESTATE", "Fork", "GIFTCOFB", "Gimli", "Grain", "HyENA", "ISAP", "KNOT", "LOTUS", "LOCUS", "mixFeed", "ORANGE",
				"Oribatida", "PHOTONBeetle", "Pyjamask", "Romulus", "SAEAES", "Saturnin", "SKINNY", "SCHWAEMM",
				"SPIX", "SpoC", "Spook", "Subterranean", "SUNDAEGIFT", "TinyJAMBU", "WAGE", "Xoodyak" };
		String[] AEADname = new String[AEADnameTemp.length];
		System.arraycopy(AEADnameTemp,0,AEADname ,0,AEADnameTemp.length);
		for(i=0;i<AEADname.length;i++ ) {
			System.out.println(i+"  "+AEADname[i]);									
		}
		int AEADnameLen=AEADnameTemp.length;
		System.out.println(AEADnameTemp.length);
		try {
			// read
			InputStreamReader reader = new InputStreamReader(new FileInputStream(feileName));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String lineTemp = "";
			while ((line = br.readLine()) != null) {
				//一个机器的开始标志
				txtname="";
				if (line.matches("^(?i)(.*<h2 id=.*)")) {
					System.out.println(line);
					//除去链接a href="hp://bench.cr.yp.to/web-impl/amd64-r24000-crypto_aead.html">
					lineTemp=line.replaceAll((String)(line.subSequence(line.indexOf("; <a href=\"h"), line.indexOf("html\">")+7)),"");
					//根据id=，;，tt进行分割
					split_result = lineTemp.split("id=|;|tt");
					//连接形成txt名字  。split_result[0]为<h2 ;   split_result[split_result.length-1]为</h2>
					for (j = 1; j < split_result.length-1; j++) {
						txtname = txtname.concat(split_result[j].replaceAll("\\W", "_"));
					}
					System.out.println(txtname);
					// write  新建该目录下的该名称的html文档
					File file = new File(f, txtname + ".html");
					FileOutputStream fileOutputStream = null;
					if (file.exists()) {
						// 判断文件是否存在，如果不存在就新建一个txt
						file.createNewFile();
					}
					fileOutputStream = new FileOutputStream(file);
					//html的基本代码
					fileOutputStream.write(startStr.getBytes());
					//写入<h2 id= 内容
					fileOutputStream.write(line.getBytes());
					fileOutputStream.write("\r\n".getBytes());
					// 一次读入一行数据，判断下一句是否为Cycles/byte，即Cycles/byte的前一句是<table border=\"\">
					while ((line = br.readLine()) != null && !line.matches("<table border=\"\">")) {
							fileOutputStream.write(line.getBytes());
							fileOutputStream.write("\r\n".getBytes());
					}
					//写数据 <table border=\"\">
					fileOutputStream.write(line.getBytes());
					fileOutputStream.write("\r\n".getBytes());
					//</tr></tbody></table>为结束标志
					while ((line = br.readLine()) != null && !line.matches("</tr></tbody></table>")) {
						if (line.matches("^(?i)(.*Cycles/byte.*)")) {
							//写数据 Cycles/byte
								fileOutputStream.write(line.getBytes());
								fileOutputStream.write("\r\n".getBytes());
								AEADnameLen=AEADnameTemp.length;
								//用AEADnameTemp初始化AEADname
								System.arraycopy(AEADnameTemp,0,AEADname ,0,AEADnameTemp.length);
							if (line.matches("^(?i)(.*encrypt.*)")) {
								System.out.println(line);
								//</tbody></table>为Cycles/byte的结束标志
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													//将字体加粗
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
													//字体转换为大写
												//	line=line.toUpperCase();
													fileOutputStream.write(line.getBytes());
													fileOutputStream.write("\r\n".getBytes());	
													}
												}
											}
										}
									}
								}
								
								for(i=0;i<AEADname.length;i++ ) {
									if(AEADname[i]!=null)
									System.out.println(i+"  "+AEADname[i]);									
								}
								
							}
							if (line.matches("^(?i)(.*decrypt.*)")) {
								System.out.println(line);
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
													//line=line.toUpperCase();
													fileOutputStream.write(line.getBytes());
													fileOutputStream.write("\r\n".getBytes());	
													}
												}
											}
										}
									}
								}
							}
							if (line.matches("^(?i)(.*forgery.*)")) {
								System.out.println(line);
								while ((line = br.readLine()) != null && !(line.matches("</tbody></table>"))) {
									if (line.matches(regex)) {
										if(AEADnameLen>0) {
											for(i=0;i<AEADname.length;i++ ) {
												if(AEADname[i]!=null) {
													if(line.matches("^(?i)(.*"+AEADname[i]+".*)$")){
													AEADnameLen--;
													AEADname[i]=null;
													line=line.replace("<tt>", "<p>");
													line=line.replace("</tt>", "</p>");
												//	line=line.toUpperCase();
													fileOutputStream.write(line.getBytes());
													fileOutputStream.write("\r\n".getBytes());	
													}
												}
											}
										}
									}
								}
								fileOutputStream.write("</tbody></table></td><td><table border=\"\">".getBytes());
								fileOutputStream.write("\r\n".getBytes());
								
							}
						}
					}
					fileOutputStream.write(endStr.getBytes());
					fileOutputStream.flush();
					fileOutputStream.close();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		//"./src/supercop3"   ：存放的文件夹  ；".\\src\\supercop_data.txt"  ：supercop的html源代码
		supercopTimingData("./src/supercopFinall_1", ".\\src\\supercop_data.txt",true);
		//supercopTimingData("./src/supercop1False", ".\\src\\test1_result.txt",false);
		//supercopTimingData("./src/supercop1True", ".\\src\\test1_result.txt",true);
		//supercopTimingData("./src/supercop1True12222", ".\\src\\test1_result.txt",true);
		//public static void supercopTimingDataFast(String dirpath, String pathname) {
		//supercopTimingDataFast("./src/FastSupercop1", ".\\src\\supercop_data.txt");
	}

}
