package com.invoicesync.modules.auth.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.core.exception.InvalidTokenException;
import com.invoicesync.modules.auth.model.EmailVerification;
import com.invoicesync.modules.auth.repository.EmailVerificationRepository;
import com.invoicesync.modules.auth.security.RefreshTokenUtil;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

@Service
@NullMarked
@Transactional
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

  private final EmailVerificationRepository emailVerificationRepository;

  private final UserRepository userRepository;

  private final JavaMailSender emailSender;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  public String createVerificationToken(final UUID userId) {
    final String rawToken = RefreshTokenUtil.generate();
    final String tokenHash = RefreshTokenUtil.hash(rawToken);

    final EmailVerification newEmailVerification = new EmailVerification();
    newEmailVerification.setTokenHash(tokenHash);
    newEmailVerification.setUserId(userId);
    newEmailVerification.setExpiresAt(LocalDateTime.now().plusMinutes(15));
    newEmailVerification.setUsed(false);

    emailVerificationRepository.save(newEmailVerification);

    return rawToken;
  }

  @Override
  public void sendVerificationEmail(final UUID userId, final String email) {

    final String token = createVerificationToken(userId);
    final String verifyUrl = frontendUrl + "/verify-email?token=" +
        URLEncoder.encode(token, StandardCharsets.UTF_8);

    final String subject = "Potvrďte svoju e-mailovú adresu – InvoiceSync";
    final String html = buildVerificationEmailHtml(verifyUrl);

    try {
      final MimeMessage mimeMessage = emailSender.createMimeMessage();
      final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
      helper.setFrom("InvoiceSync <jfelber2001@gmail.com>");
      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(html, true);
      emailSender.send(mimeMessage);
    } catch (final MessagingException e) {
      throw new MailPreparationException("Failed to build verification email", e);
    }
  }

  private String buildVerificationEmailHtml(final String verifyUrl) {
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
                        <h2 style="margin:0 0 18px; font-size:20px; color:#16213a;">Potvrďte svoju e-mailovú adresu</h2>
                        <p style="margin:0 0 16px; font-size:15px; line-height:1.6; color:#16213a;">Dobrý deň,</p>
                        <p style="margin:0 0 16px; font-size:15px; line-height:1.6; color:#16213a;">dostávate tento e-mail, pretože bola vaša e-mailová adresa použitá na registráciu v InvoiceSync.</p>
                        <p style="margin:0 0 24px; font-size:15px; line-height:1.6; color:#16213a;">Pre dokončenie registrácie potvrďte svoju e-mailovú adresu kliknutím na tlačidlo nižšie:</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 24px;">
                        <a href="%s" style="display:inline-block; background-color:#1f6f5c; color:#ffffff; text-decoration:none; font-weight:bold; font-size:15px; padding:13px 26px; border-radius:8px;">Potvrdiť e-mailovú adresu</a>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 18px;">
                        <p style="margin:0; font-size:13px; color:#5b6478; word-break:break-all;">Ak tlačidlo nefunguje, skopírujte tento odkaz do prehliadača:<br /><a href="%s" style="color:#1f6f5c;">%s</a></p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:0 40px 28px;">
                        <p style="margin:0; font-size:13px; color:#5b6478;">Odkaz je platný 15 minút. Ak ste o registráciu nežiadali, tento e-mail ignorujte.</p>
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
        """.formatted(verifyUrl, verifyUrl, verifyUrl);
  }

  @Override
  public void verifyEmail(final String token) {
    final String raw = RefreshTokenUtil.hash(token);
    final EmailVerification newEmailVerification = emailVerificationRepository.findByTokenHash(raw).orElse(null);

    if (newEmailVerification == null) {
      throw new InvalidTokenException("Not an access token");
    }

    if (newEmailVerification.isUsed() || newEmailVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new InvalidTokenException("Token has been used or expired");
    }

    final User user = userRepository.findById(newEmailVerification.getUserId()).orElse(null);
    if  (user == null) {
      throw new InvalidTokenException("User not found");
    }

    newEmailVerification.setUsed(true);
    user.setEmailVerified(true);
    userRepository.save(user);
    emailVerificationRepository.save(newEmailVerification);
  }

}
