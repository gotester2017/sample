package com.sample;

import com.google.gson.*;
import com.google.gson.reflect.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class WebService 
{
	
    public List<Reward> getRewardsByPerson(List<Voucher> vouchers, List<Credit> credits){

	//Sort vouchers numbers by firstname+lastname in a map which maintains order
		LinkedHashMap vmap = new LinkedHashMap();
		LinkedHashSet<String> vouchernumber = new LinkedHashSet<String>();

		for(int i=0 ; i < vouchers.size() ; i++){

			if(!vmap.containsKey(vouchers.get(i).getFirstname()+"|"+vouchers.get(i).getLastname()))
			{		
				vouchernumber.add(vouchers.get(i).getVouchernumber());
				vmap.put(vouchers.get(i).getFirstname()+"|"+vouchers.get(i).getLastname(), vouchernumber.toArray(new String[vouchernumber.size()]) );					
				
			}
			for(int j=i+1 ; j < vouchers.size() ; j++){

				String occurence = vouchers.get(i).getFirstname()+"|"+vouchers.get(i).getLastname();
				String recurrence  = vouchers.get(j).getFirstname()+"|"+vouchers.get(j).getLastname();
				if(occurence.equals(recurrence)){
	
					vouchernumber.add(vouchers.get(j).getVouchernumber());
					vmap.put(vouchers.get(j).getFirstname()+"|"+vouchers.get(j).getLastname(), vouchernumber.toArray(new String[vouchernumber.size()]) );		
				}
	
				
			}

			vouchernumber.clear();

		}
		
	//Sort credit id by firstname+lastname in a map which maintains order

		LinkedHashMap cmap = new LinkedHashMap();
		LinkedHashSet<String> creditid = new LinkedHashSet<String>();

		for(int i=0 ; i < credits.size() ; i++){

			if(!cmap.containsKey(credits.get(i).getFirstname()+"|"+credits.get(i).getLastname()))
			{
				creditid.add(credits.get(i).getCreditid());
				cmap.put(credits.get(i).getFirstname()+"|"+credits.get(i).getLastname(), creditid.toArray(new String[creditid.size()]) );
				
			}
			for(int j=i+1 ; j < credits.size() ; j++){
				String occurence= credits.get(i).getFirstname()+"|"+credits.get(i).getLastname();
				String recurrence= credits.get(j).getFirstname()+"|"+credits.get(j).getLastname();
				if(occurence.equals(recurrence)){
	
					creditid.add(credits.get(j).getCreditid());
					cmap.put(credits.get(j).getFirstname()+"|"+credits.get(j).getLastname(), creditid.toArray(new String[creditid.size()]) );		
				}
	
				
			}
			
			creditid.clear();
		}
				
// Create the combined list of awards from vouchers and credits								
		ArrayList<Reward> rewards = new ArrayList<Reward>();

		Iterator<Map.Entry<String,String[]>> iterator = vmap.entrySet().iterator();
		while (iterator.hasNext()) {
			
			Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>) iterator.next();

			Reward reward = new Reward();
			reward.setVoucher(entry.getValue());

			StringTokenizer token = new StringTokenizer(entry.getKey(), "|");
			reward.setFirstname(token.nextToken());
			reward.setLastname(token.nextToken());
			rewards.add(reward);

		}

		boolean flag = false;

		Iterator<Map.Entry<String,String[]>> citerator = cmap.entrySet().iterator();
		while (citerator.hasNext()) {

			Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>) citerator.next();
			
			for(int k =0 ; k < rewards.size(); k++)
			{
				if(entry.getKey().equals(rewards.get(k).getFirstname()+"|"+rewards.get(k).getLastname()))
				{					
					rewards.get(k).setCredit(entry.getValue() == null ? new String[]{} : entry.getValue());
					flag = true;
				}

			}			
			
			if(!flag)
			{
				Reward reward = new Reward();
				reward.setCredit(entry.getValue() == null ? new String[]{} : entry.getValue());
				StringTokenizer token = new StringTokenizer(entry.getKey(), "|");
				reward.setFirstname(token.nextToken());
				reward.setLastname(token.nextToken());
			
				rewards.add(reward);

				flag = false;
			}

		}

		return rewards;
	}

	public static void main(String[] args) 
	{
		
		ArrayList<Voucher> vouchers = new ArrayList<Voucher>();

		Gson gson = new Gson();

        try (Reader reader = new FileReader("D:\\sample\\voucher.json")) {


			// Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
			
			//String jsontype = "[{\"voucher-number\":\"V2378578346\", \"voucher-value\":\"1200\", \"currency\": \"INR\", \"firstname\": \"Drew\", \"lastname\": \"Barrymore\"}]";
			Type collectionType = new TypeToken<List<Voucher>>(){}.getType();
		    vouchers = gson.fromJson(jsonInString, collectionType);
		
        } catch (IOException e) {
            e.printStackTrace();
        }

		ArrayList<Credit> credits = new ArrayList<Credit>();
		
		try (Reader reader = new FileReader("D:\\sample\\credit.json")) {

			// Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
			
			//String jsontype = "[{\"voucher-number\":\"V2378578346\", \"voucher-value\":\"1200\", \"currency\": \"INR\", \"firstname\": \"Drew\", \"lastname\": \"Barrymore\"}]";
			Type collectionType = new TypeToken<List<Credit>>(){}.getType();
		    credits = gson.fromJson(jsonInString, collectionType);
			
		
        } catch (IOException e) {
            e.printStackTrace();
        }

		WebService service = new WebService();

		//Returns list of rewards by person
		List<Reward> rewards = service.getRewardsByPerson(vouchers, credits);


		 //Convert object to JSON string
        String json = gson.toJson(rewards);

        //Convert object to JSON string and save into a file directly
        try (FileWriter writer = new FileWriter("D:\\sample\\rewards.json")) {

            gson.toJson(rewards, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }

		
	}
}
