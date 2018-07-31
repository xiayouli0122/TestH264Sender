package com.harsom.baselib.glide.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * @author 工藤
 * @emil gougou@16fan.com
 * com.fan16.cn.loader.glide
 * create at 2018/5/10  11:11
 * description:
 */
public class SafeKeyGenerator {
	private final LruCache<Key, String> loadIdToSafeHash = new LruCache<Key, String>(1000);

	public String getSafeKey(Key key) {
		String safeKey;
		synchronized (loadIdToSafeHash) {
			safeKey = loadIdToSafeHash.get(key);
		}
		if (safeKey == null) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				key.updateDiskCacheKey(messageDigest);
				safeKey = Util.sha256BytesToHex(messageDigest.digest());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			synchronized (loadIdToSafeHash) {
				loadIdToSafeHash.put(key, safeKey);
			}
		}
		return safeKey;
	}
}