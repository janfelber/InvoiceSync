package com.invoicesync.modules.auth.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.core.exception.InvalidTokenException;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.auth.model.PasswordReset;
import com.invoicesync.modules.auth.repository.PasswordResetRepository;
import com.invoicesync.modules.auth.security.RefreshTokenUtil;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@NullMarked
@Transactional
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

  private final RefreshSessionService refreshSessionService;

  private final UserActivityService userActivityService;

  private final UserRepository userRepository;

  private final PasswordResetRepository passwordResetRepository;

  private final JavaMailSender emailSender;

  private final PasswordEncoder passwordEncoder;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  public String createVerificationToken(final String email) {
    final String rawToken = RefreshTokenUtil.generate();
    final String hashToken = RefreshTokenUtil.hash(rawToken);

    final User user = userRepository.findByEmailIgnoreCase(email.trim()).orElseThrow();

    final PasswordReset newPasswordReset = new PasswordReset();
    newPasswordReset.setTokenHash(hashToken);
    newPasswordReset.setUserId(user.getId());
    newPasswordReset.setExpiresAt(LocalDateTime.now().plusMinutes(15));
    newPasswordReset.setUsed(false);

    passwordResetRepository.save(newPasswordReset);

    return rawToken;
  }

  @Override
  public void sendPasswordForgotEmail(final String email) {
    final String token = createVerificationToken(email);
    final String resetUrl = frontendUrl + "/reset-password?token=" +
        URLEncoder.encode(token, StandardCharsets.UTF_8);

    final String subject = "Obnovte svoje heslo – InvoiceSync";
    final String html = buildPasswordResetEmailHtml(resetUrl);

    try {
      final MimeMessage mimeMessage = emailSender.createMimeMessage();
      final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
      helper.setFrom("InvoiceSync <jfelber2001@gmail.com>");
      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(html, true);
      emailSender.send(mimeMessage);
    } catch (final MessagingException e) {
      throw new MailPreparationException("Failed to build password reset email", e);
    }
  }

  @Override
  public void resetPassword(final String token, final String newPassword) {
    String hashToken = RefreshTokenUtil.hash(token);
    final PasswordReset passwordReset = passwordResetRepository.findByTokenHash(hashToken).orElse(null);

    if (passwordReset == null) {
      throw new InvalidTokenException("Not an access token");
    }

    if (passwordReset.isUsed() || passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException("Token has expired");
    }

    final User user = userRepository.findById(passwordReset.getUserId()).orElseThrow();

    passwordReset.setUsed(true);
    user.setPassword(passwordEncoder.encode(newPassword));

    refreshSessionService.revokeAllForUser(user.getId());
    passwordResetRepository.save(passwordReset);
    userRepository.save(user);
    userActivityService.save(UserActivityRecord.forType(user.getId().toString(), UserActivityType.PASSWORD_RESET,
        "Password reset via forgot-password flow"));
  }

  private String buildPasswordResetEmailHtml(final String resetUrl) {
    return """
        <!doctype html>
        <html lang="sk">
          <body style="margin:0; padding:0; background-color:#eef0f6;">
            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#eef0f6; padding:32px 0;">
              <tr>
                <td align="center">
                  <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:12px; overflow:hidden; font-family:Arial, Helvetica, sans-serif;">
                    <tr>
                      <td style="padding:32px 40px 0;">
                        <span style="font-size:18px; font-weight:bold; color:#16213a;">Invoice<span style="color:#1f6f5c;">Sync</span></span>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:24px 40px 0;">
                        <h2 style="margin:0 0 18px; font-size:20px; color:#16213a;">Obnovte svoje heslo</h2>
                        <p style="margin:0 0 16px; font-size:15px; line-height:1.6; color:#16213a;">Dobrý deň,</p>
                        <p style="margin:0 0 16px; font-size:15px; line-height:1.6; color:#16213a;">dostali sme žiadosť o obnovenie hesla pre váš účet InvoiceSync.</p>
                        <p style="margin:0 0 24px; font-size:15px; line-height:1.6; color:#16213a;">Pre nastavenie nového hesla kliknite na tlačidlo nižšie:</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 24px;">
                        <a href="%s" style="display:inline-block; background-color:#1f6f5c; color:#ffffff; text-decoration:none; font-weight:bold; font-size:15px; padding:13px 26px; border-radius:8px;">Nastaviť nové heslo</a>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 18px;">
                        <p style="margin:0; font-size:13px; color:#5b6478; word-break:break-all;">Ak tlačidlo nefunguje, skopírujte tento odkaz do prehliadača:<br /><a href="%s" style="color:#1f6f5c;">%s</a></p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 28px;">
                        <p style="margin:0; font-size:13px; color:#5b6478;">Odkaz je platný 15 minút. Ak ste o obnovenie hesla nežiadali, tento e-mail ignorujte — vaše heslo zostáva nezmenené.</p>
                        <hr style="border:none; border-top:1px solid #e3e6ee; margin:24px 0 20px;" />
                        <p style="margin:0; font-size:15px; font-weight:bold; color:#16213a;">Tím InvoiceSync</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:18px 40px; background-color:#f7f8fb; border-top:1px solid #e3e6ee;">
                        <p style="margin:0; font-size:12px; color:#8790a3; line-height:1.6;">Tento e-mail bol odoslaný automaticky, neodpovedajte naň priamo.<br />InvoiceSync</p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(resetUrl, resetUrl, resetUrl);
  }

}
