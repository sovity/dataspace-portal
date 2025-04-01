<#import "template.ftl" as layout>
<#import "password-commons.ftl" as passwordCommons>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('password','password-confirm'); section>
    <#if section = "header">
        ${msg("updatePasswordTitle")}
    <#elseif section = "form">
        <form id="kc-passwd-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <input type="password" id="password-new" name="password-new" class="text-input"
                       autofocus autocomplete="new-password" placeholder="New Password"
                       aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                />
            </div>

            <#if messagesPerField.existsError('password')>
                <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('password'))?no_esc}
                        </span>
            </#if>

            <div class="${properties.kcFormGroupClass!}">
                <input type="password" id="password-confirm" name="password-confirm"
                       class="text-input"
                       autocomplete="new-password" placeholder="Confirm Password"
                       aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                />
            </div>

            <#if messagesPerField.existsError('password-confirm')>
                <span id="input-error-password-confirm" class="${properties.kcInputErrorMessageClass!}"
                      aria-live="polite">
                            ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                        </span>
            </#if>

            <div class="${properties.kcFormGroupClass!}">
                <@passwordCommons.logoutOtherSessions/>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <#if isAppInitiatedAction??>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonBlockClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                        <button
                        class="${properties.kcButtonClass!} ${properties.kcButtonCancelClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonBlockClass!}"
                        type="submit" name="cancel-aia" value="true" />${msg("doCancel")}</button>
                    <#else>
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonFullWidthClass!}"
                               type="submit" value="${msg("doSubmit")}"/>
                    </#if>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>