package org.aksw.autosparql.tbsl.algorithm.templator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.aksw.autosparql.tbsl.algorithm.sem.util.Pair;

public class BasicSlotBuilder {

	private String[] noun = {"NN","NNS","NNP","NNPS","NPREP","JJNN","JJNPREP"};
	private String[] adjective = {"JJ","JJR","JJS","JJH"};
	private String[] verb = {"VB","VBD","VBG","VBN","VBP","VBZ","PASSIVE","PASSPART","VPASS","VPASSIN","GERUNDIN","VPREP","WHEN","WHENPREP","WHERE"};
	private String[] preps = {"IN","TO"};

	public BasicSlotBuilder() {
	}

	/**
	 *  gets synonyms, attribute etc. from WordNet and construct grammar entries
	 *  INPUT:  array of tokens and array of POStags, from which preprocessor constructs a list of pairs (token,pos)
	 *  OUTPUT: list of (treestring,dude)
	 **/
	public List<String[]> build(String taggedstring,List<Pair<String,String>> tokenPOSpairs) {

		List<String[]> result = new ArrayList<String[]>();

		for (Pair<String,String> pair : tokenPOSpairs) {

			String token = pair.fst;
			String tokenfluent = token.replaceAll(" ","").replaceAll("_","");
			String pos = pair.snd;

			String type = "UNSPEC";
			String slot;

                        /* PRONOUN HACK */
                        if (pos.equals("PRP") || pos.equals("PRP$")) {
                            String[] pronEntry = {token,
                                        "(DET DET:'" + token.toLowerCase() + "')",
                                        "<x,l1,e,[ l1:[ x | ] ],[],[],[]>"};
                            result.add(pronEntry);
                        }

			/* NOUNS */
			if (equalsOneOf(pos,noun)) {

				if (pos.equals("NNP") || pos.equals("NNPS")) {
					type = "RESOURCE";
				}

				slot = "SLOT_" + tokenfluent + "/" + type + "/" + token;

				// treetoken
				String treetoken = "N:'" + token.toLowerCase() + "'";
				if (token.trim().contains(" ")) {
					String[] tokenParts = token.split(" ");
					treetoken = "";
					for (String t : tokenParts) {
						treetoken += " N:'" + t.toLowerCase() + "'";
					}
					treetoken = treetoken.trim();
				}
				//
				if (pos.equals("NN") || pos.equals("NNS")) {
					/* DP */
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slot + "]>"};
					String[] dpEntry2 = {token,
							"(DP (NP " + treetoken + " DP[name]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x), equal(x,y) ] ],[ (l2,y,name,<<e,t>,t>) ],[l2=l1],[" + slot + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
					/* NP */
					String[] npEntry1 = {token,
							"(NP " + treetoken + ")",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slot + "]>"};
					String[] npEntry2 = {token,
							"(NP " + treetoken + " DP[name])",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x), equal(x,y) ] ],[ (l2,y,name,<<e,t>,t>) ],[l2=l1],[" + slot + "]>"};
					result.add(npEntry1);
					result.add(npEntry2);
				}
				else if (pos.equals("NNP") || pos.equals("NNPS")) {
					/* DP */
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slot + "]>"};
					String[] dpEntry2 = {token,
							"(DP DET[det] (NP " + treetoken + "))",
							"<x,l1,<<e,t>,t>,[ l1:[ | SLOT_" + tokenfluent + "(x) ] ],[(l2,x,det,e)],[l2=l1],[" + slot + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
				}
				else if (pos.equals("NPREP")) {
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slot + "]>"};
					String[] dpEntry2 = {token,
							"(DP DET[det] (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>),(l3,x,det,e)],[l2=l1,l3=l1],[" + slot + "]>"};
					String[] npEntry = {token,
							"(NP " + treetoken + " DP[pobj])",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slot + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
					result.add(npEntry);
				}
				else if (pos.equals("JJNPREP")) {
					slot = "SLOT_" + tokenfluent + "/UNSPEC/" + token;
					String[] dpEntry1 = {token,
							"(DP (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ x,p | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slot + "]>" };
					String[] dpEntry2 = {token,
							"(DP DET[det] (NP " + treetoken + " DP[pobj]))",
							"<x,l1,<<e,t>,t>,[ l1:[ p | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>),(l3,x,det,e)],[l2=l1,l3=l1],[" + slot + "]>" };
					String[] npEntry = {token,
							"(NP " + treetoken + " DP[pobj])",
							"<x,l1,<e,t>,[ l1:[ p | SLOT_" + tokenfluent + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[" + slot + "]>"};
					result.add(dpEntry1);
					result.add(dpEntry2);
					result.add(npEntry);
				}
				else if(pos.equals("JJNN") && token.contains("_")) {
					slot = "SLOT_" + tokenfluent + "/UNSPEC/" + token;
					String[] npEntry = {token,
							"(NP " + treetoken + " )",
							"<x,l1,<e,t>,[ l1:[ | SLOT_" + tokenfluent + "(x) ] ],[],[],[" + slot + "]>"};
					result.add(npEntry);
				}

			}
			/* VERBS */
			else if (equalsOneOf(pos,verb)) {

				if (token.equals("has") || token.equals("have") || token.equals("had")) {
					slot = "";
				}
				else {
					slot = "SLOT_" + token + "/PROPERTY/" + token;
				}
				if (pos.equals("PASSIVE")) {
					String[] passEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					String[] passEntry2 = {token,
							"(S DP[wh] (VP DP[dp] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(y,x) ] ],[(l2,x,wh,<<e,t>,t>),(l3,y,dp,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,wh,<<e,t>,t>),(l3,y,dp,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					result.add(passEntry1);
					result.add(passEntry2);
				}
				else if (pos.equals("PASSPART")) {
					String[] passpartEntry = {token,
							"(NP NP* (VP V:'" + token + "' DP[dp]))",
							"<x,l1,t,[ l1:[ p | SLOT_" + token + "(y,x) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[ | empty(y,x) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[]>"};
					result.add(passpartEntry);
				}
				else if (pos.equals("VPASS")) {
					String[] passEntry = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					result.add(passEntry);
				}
				else if (pos.equals("VPASSIN")) {
					String[] passEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					String[] passEntry2 = {token,
							"(S DP[dp] (VP V:'" + token + "' NUM[num]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(y,z) ] ],[(l2,x,dp,<<e,t>,t>),(l3,z,num,e)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					result.add(passEntry1);
					result.add(passEntry2);
				}
				else if (pos.equals("GERUNDIN")) {
					String[] gerundinEntry1 = {token,
							"(NP NP* V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[ p | SLOT_" + token + "(y,x) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[ | empty(x,y) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[]>"};
					String[] gerundinEntry2 = {token,
							"(ADJ V:'" + token + "' DP[obj]))",
							"<x,l1,<e,t>,[ l1:[ p | SLOT_" + token + "(y,x) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>" +
									" ;; <x,l1,<e,t>,[ l1:[ | empty(x,y) ] ],[(l2,y,obj,<<e,t>,t>)],[ l2=l1 ],[]>"};
					result.add(gerundinEntry1);
					result.add(gerundinEntry2);
				}
				else if (pos.equals("VPREP")) {
					String[] passEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					String[] passEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' NUM[num]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y), DATE(y,z) ] ],[(l2,x,subj,<<e,t>,t>),(l3,z,num,e)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>"};
					String[] whEntry = {token,
							"(S DP[obj] (VP DP[subj] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					result.add(passEntry1);
					result.add(passEntry2);
					result.add(whEntry);
				}
				else if (pos.equals("VBD") || pos.equals("VBZ") || pos.equals("VBP")) {
					String[] vEntry = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(y,x) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					result.add(vEntry);
				}
				else if (pos.equals("VB")) {
					String[] whEntry1 = {token,
							"(S DP[obj] (VP DP[subj] V:'" + token + "'))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
                                        String[] whEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj] ))",
							"<x,l1,t,[ l1:[|], l4:[ p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[" + slot + "]>" +
									" ;; <x,l1,t,[ l1:[|], l4:[ | empty(x,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,y,obj,<<e,t>,t>)],[ l2<l1,l3<l1,l4<scope(l2),l4<scope(l3) ],[]>"};
					result.add(whEntry1);
					result.add(whEntry2);
				}
				else if (pos.equals("VBG") || pos.equals("VBN")) {
					String[] gerEntry = {token,
							"(NP NP* (VP V:'" + token + "' DP[dp]))",
							"<x,l1,t,[ l1:[ p | SLOT_" + token + "(x,y) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[" + slot + "]>" +
									";; <x,l1,t,[ l1:[ | empty(x,y) ] ],[(l2,y,dp,<<e,t>,t>)],[ l2=l1 ],[]>"};
					String[] wasGerEntry = {token,
							"(S DP[comp] (VP V:'was' DP[subject] V:'" + token + "'))",
							"<y,l1,t,[ l1:[ | SLOT_" + token + "(y,z) ] ],[(l2,y,comp,<<e,t>,t>), (l3,z,subject,<<e,t>,t>) ],[ l2=l1, l3=l1 ],[" + slot + "]>"};
					result.add(wasGerEntry);
					result.add(gerEntry);
				}
				else if (pos.equals("WHEN")) {
					slot = "SLOT_" + token + "/PROPERTY/" + token + "_date";
					String[] whenEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[ ?y,p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>)],[ l2=l1 ],[ " + slot + " ]>"};
                                        String[] whenEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[obj]))",
							"<x,l1,t,[ l1:[ ?y,p | SLOT_" + token + "(x,z,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,z,obj,<<e,t>,t>)],[ l2=l1,l3=l1 ],[ " + slot + " ]>"};
					result.add(whenEntry1);
					result.add(whenEntry2);
				}
                                else if (pos.equals("WHENPREP")) {
                                    System.out.println(" >>>> " + token); // DEBUG
					slot = "SLOT_" + token + "/PROPERTY/" + token + "_date";
					String[] whenprepEntry1 = {token,
							"(S DP[subj] (VP V:'" + token + "' DP[pobj]))",
							"<x,l1,t,[ l1:[ ?y,p | SLOT_" + token + "(x,z,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,z,pobj,<<e,t>,t>)],[ l2=l1,l3=l1 ],[ " + slot + " ]>"};
                                        String[] whenprepEntry2 = {token,
							"(S DP[subj] (VP V:'" + token + "' NP[pobj]))",
							"<x,l1,t,[ l1:[ ?y,p,z | SLOT_" + token + "(x,z,y) ] ],[(l2,x,subj,<<e,t>,t>),(l3,z,pobj,<e,t>)],[ l2=l1,l3=l1 ],[ " + slot + " ]>"};
					result.add(whenprepEntry1);
					result.add(whenprepEntry2);
				}
				else if (pos.equals("WHERE")) {
					slot = "SLOT_" + token + "/PROPERTY/" + token + "_place";
					String[] whereEntry = {token,
							"(S DP[subj] (VP V:'" + token + "'))",
							"<x,l1,t,[ l1:[ ?y,p | SLOT_" + token + "(x,y) ] ],[(l2,x,subj,<<e,t>,t>)],[ l2=l1 ],[ " + slot + " ]>"};
					result.add(whereEntry);
				}

			}
			/* ADJECTIVES */
			else if (equalsOneOf(pos,adjective)) {

				slot = "SLOT_" + token + "/PROPERTY/" + token;
				/* ADJECTIVE */
				if (pos.equals("JJ")) {
					String[] adjEntry1 = {token,
							"(NP ADJ:'" + token.toLowerCase() + "' NP*)",
							"<x,l1,<e,t>,[ l1:[ j | SLOT_" + token + "(x,j) ] ],[],[],["+slot+"]>"};
//                                        String[] adjEntry2 = {"is .+ " + token,
//							"(S DP[subject] (VP V:'is' ADJ:'" + token.toLowerCase() + "'))",
//							"<x,l1,<e,t>,[ l1:[ | SLOT_" + token + "(x) ] ],[(l2,x,subject,<<e,t>,t>)],[l2=l1],["+slot+"]>"};
//                                        String[] adjEntry3 = {"is .+ " + token,
//							"(S (VP V:'is' DP[subject] ADJ:'" + token.toLowerCase() + "'))",
//							"<x,l1,<e,t>,[ l1:[ | SLOT_" + token + "(x) ] ],[(l2,x,subject,<<e,t>,t>)],[l2=l1],["+slot+"]>"};
					result.add(adjEntry1);
//					result.add(adjEntry2);
//					result.add(adjEntry3);
				}
				if (pos.equals("JJH")) {
					String[] howEntry = {"how "+token,
							"(DP ADJ:'" + token.toLowerCase() + "')",
							"<x,l1,<<e,t>,t>,[ l1:[ ?j,x,p | SLOT_" + token + "(x,j) ] ],[],[],["+slot+"]>"};
					result.add(howEntry);
				}
				/* COMPARATIVE */
				else if (pos.equals("JJR")) {
					String pol = polarity(token);
					String comp;
					if (pol.equals("POS")) {
						comp = "greater";
					} else { comp = "less"; }

					String[] compEntry1 = {token,
							"(ADJ ADJ:'" + token.toLowerCase() + "' P:'than' DP[compobj])",
							"<x,l1,<e,t>,[ l1:[ p,j,i | SLOT_" + token + "(x,i), SLOT_" + token + "(y,j), " + comp + "(i,j) ] ],[ (l2,y,compobj,<<e,t>,t>) ],[l1=l2],["+slot+"]>"};
					result.add(compEntry1);
					String[] compEntry2 = {token,
							"(NP NP* (ADJ ADJ:'" + token.toLowerCase() + "' P:'than' DP[compobj]))",
							"<x,l1,<e,t>,[ l1:[ p,j,i | SLOT_" + token + "(x,i), SLOT_" + token + "(y,j), " + comp + "(i,j) ] ],[ (l2,y,compobj,<<e,t>,t>) ],[l1=l2],["+slot+"]>"};
					result.add(compEntry2);
				}
				/* SUPERLATIVE */
				else if (pos.equals("JJS")) {
					String pol = polarity(token);
					String comp;
					if (pol.equals("POS")) {
						comp = "maximum";
					} else { comp = "minimum"; }

					String[] superEntry1 = {token,
							"(DET DET:'the' ADJ:'" + token.toLowerCase() + "')",
							"<x,l1,e,[ l1:[ p,x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[],[],["+slot+"]>"};
					result.add(superEntry1);
					String[] superEntry2 = {token,
							"(DP (NP DET:'the' ADJ:'" + token.toLowerCase() + "'))",
							"<x,l1,<<e,t>,t>,[ l1:[ p,x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[],[],["+slot+"]>"};
					result.add(superEntry2);
					String[] superEntry3 = {token,
							"(DP (NP DET:'the' ADJ:'" + token.toLowerCase() + "' NP[noun]))",
							"<x,l1,<<e,t>,t>,[ l1:[ p,x,j | SLOT_" + token + "(x,j), " + comp + "(j) ] ],[ (l2,x,noun,<e,t>) ],[l2=l1],["+slot+"]>"};
					result.add(superEntry3);
				}
			}
			/* PREPOSITIONS */
			else if (equalsOneOf(pos,preps)) {
				slot = "SLOT_" + token + "/PROPERTY/";
				String[] npAdjunct = {token,
						"(NP NP* (PP P:'" + token.toLowerCase() + "' DP[pobj]))",
						"<x,l1,<e,t>,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],["+slot+"]>" +
								" ;; <x,l1,<e,t>,[ l1:[ | empty(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[]>"};
				String[] vpAdjunct = {token,
						"(VP VP* (PP P:'" + token.toLowerCase() + "' DP[pobj]))",
						"<x,l1,t,[ l1:[ | SLOT_" + token + "(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],["+slot+"]>" +
								" ;; <x,l1,t,[ l1:[ | empty(x,y) ] ],[(l2,y,pobj,<<e,t>,t>)],[l2=l1],[]>"};
				result.add(npAdjunct);
				result.add(vpAdjunct);
			}
		}

		return result;
	}

	private boolean equalsOneOf(String string,String[] strings) {
		for (String s : strings) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}

	private String polarity(String adj) {

		String polarity = "POS";

		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("tbsl/lexicon/adj_list.txt")));
			String line;
			while ((line = in.readLine()) != null ) {
				if (line.contains(adj)) {
					polarity = line.split(" ")[0];
					break;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return polarity;
	}


}
