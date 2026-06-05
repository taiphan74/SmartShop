package com.ptithcm.smartshop.activity.controller;

import com.ptithcm.smartshop.activity.service.UserActivityService;
import com.ptithcm.smartshop.security.session.SessionConstants;
import com.ptithcm.smartshop.security.session.SessionUser;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WishlistController {

    private final UserActivityService userActivityService;

    public WishlistController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @PostMapping("/wishlist/products/{productId}/toggle")
    public String toggleProductWishlist(
            @PathVariable UUID productId,
            @RequestParam(name = "redirectUrl", required = false, defaultValue = "/profile/activity") String redirectUrl,
            @SessionAttribute(name = SessionConstants.CURRENT_USER, required = false) SessionUser sessionUser,
            RedirectAttributes redirectAttributes) {
        if (sessionUser == null) {
            return "redirect:/auth/login";
        }
        boolean saved = userActivityService.toggleProductWishlist(sessionUser.id(), productId);
        redirectAttributes.addFlashAttribute("successMessage", saved ? "Đã thêm sản phẩm vào Wishlist." : "Đã bỏ sản phẩm khỏi Wishlist.");
        return "redirect:" + sanitizeRedirectUrl(redirectUrl);
    }

    @PostMapping("/wishlist/shops/{shopId}/toggle")
    public String toggleShopWishlist(
            @PathVariable UUID shopId,
            @RequestParam(name = "redirectUrl", required = false, defaultValue = "/profile/activity") String redirectUrl,
            @SessionAttribute(name = SessionConstants.CURRENT_USER, required = false) SessionUser sessionUser,
            RedirectAttributes redirectAttributes) {
        if (sessionUser == null) {
            return "redirect:/auth/login";
        }
        boolean saved = userActivityService.toggleShopWishlist(sessionUser.id(), shopId);
        redirectAttributes.addFlashAttribute("successMessage", saved ? "Đã thêm cửa hàng vào Wishlist." : "Đã bỏ cửa hàng khỏi Wishlist.");
        return "redirect:" + sanitizeRedirectUrl(redirectUrl);
    }

    private String sanitizeRedirectUrl(String redirectUrl) {
        if (redirectUrl == null || redirectUrl.isBlank() || !redirectUrl.startsWith("/") || redirectUrl.startsWith("//")) {
            return "/profile/activity";
        }
        return redirectUrl;
    }
}
