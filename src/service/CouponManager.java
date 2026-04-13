package service;

import com.google.gson.reflect.TypeToken;
import model.order.Coupon;
import util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class CouponManager {
    private static CouponManager instance;
    private static final String FILE_PATH = "data/coupons.json";
    private Map<String, Coupon> coupons;

    private CouponManager() {
        coupons = new TreeMap<>(); // Using TreeMap to keep coupons sorted by code
        try {
            loadCoupons();
        } catch (IOException e) {
            System.err.println("Could not load coupons: " + e.getMessage());
        }
    }

    public static CouponManager getInstance() {
        if (instance == null) {
            instance = new CouponManager();
        }
        return instance;
    }

    private void loadCoupons() throws IOException {
        Type type = new TypeToken<Map<String, Coupon>>(){}.getType();
        Map<String, Coupon> loaded = JsonUtil.readFromFile(FILE_PATH, type);
        if (loaded != null) {
            coupons = new TreeMap<>(loaded);
        }
    }

    public void saveCoupons() throws IOException {
        JsonUtil.writeToFile(FILE_PATH, coupons);
    }

    public void addCoupon(Coupon coupon) throws IOException {
        coupons.put(coupon.getCode().toUpperCase(), coupon);
        saveCoupons();
    }

    public void toggleCouponStatus(String code) throws IOException {
        Coupon coupon = coupons.get(code.toUpperCase());
        if (coupon != null) {
            coupon.setActive(!coupon.isActive());
            saveCoupons();
        }
    }

    public Map<String, Coupon> getAllCoupons() {
        return coupons;
    }

    public Coupon validateCoupon(String code) {
        if (code == null || code.isEmpty()) return null;
        Coupon coupon = coupons.get(code.toUpperCase());
        if (coupon != null && coupon.isActive()) {
            return coupon;
        }
        return null;
    }
}
