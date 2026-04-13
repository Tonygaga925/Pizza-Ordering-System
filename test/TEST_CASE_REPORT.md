# Pizza Ordering System Test Case Report

This report explains each JUnit 5 test case in detail using a test-spec style:
- Intent: why this test exists.
- Setup and Input: key fixture state and simulated user input path.
- Expected Verification: the core assertions checked by the test.

Total test cases: 66

## Option 1 - Member Login and Member Menu
Source: Option1MemberLoginCommandTest.java

1. option1_LoginSuccessful_ShouldShowSuccessOutput
- Intent: confirm the happy-path member login works.
- Setup and Input: prepare member data with a valid username and password, then execute login command with matching credentials.
- Expected Verification: login success message is printed and command result indicates authenticated member context.

2. option1_LoginFailed_ShouldShowInvalidCredentialOutput
- Intent: ensure invalid credentials are blocked.
- Setup and Input: provide a known username with wrong password (or unknown credentials).
- Expected Verification: invalid credential message appears and login does not create authenticated session.

3. option1_LoginMinusOneAtUsername_ShouldGoBack
- Intent: validate back-navigation support at first login prompt.
- Setup and Input: enter -1 at username prompt.
- Expected Verification: flow exits login screen immediately and returns control to previous menu context.

4. option1_LoginMinusOneAtPassword_ShouldGoBackToUsername
- Intent: verify partial backtracking inside login wizard.
- Setup and Input: input valid username, then enter -1 when password is requested.
- Expected Verification: command returns to username stage rather than authenticating or hard-failing.

5. option1_LoginSuccessful_ShouldContainMemberMenuOptionsInMainFlow
- Intent: ensure post-login UI contract is complete.
- Setup and Input: perform successful login in main flow.
- Expected Verification: output includes full member menu options (1 to 6) for subsequent actions.

6. option1_LoginSuccessful_Option1ShowMenu_ShouldDisplayPizzaMenuOutput
- Intent: verify member option 1 delegates to menu display correctly.
- Setup and Input: after successful login, choose option 1.
- Expected Verification: pizza list/details are printed (menu section appears in output).

7. option1_LoginSuccessful_Option2PlaceOrderTestCase1_AddNewPizzaDifferentPizzaSizeToppingQty
- Intent: confirm add-new-item subflow builds full order item correctly.
- Setup and Input: select place-order, then choose pizza type, size, topping selections, and quantity.
- Expected Verification: cart/order includes the newly built item with selected attributes and expected pricing/quantity output.

8. option1_LoginSuccessful_Option2PlaceOrderTestCase2_ModifyExistingPizzaQuantity
- Intent: verify quantity edit logic on existing cart line.
- Setup and Input: start with at least one item in order, choose modify sub-option, then provide new quantity.
- Expected Verification: item quantity changes to target value and updated total/cart summary is reflected.

9. option1_LoginSuccessful_Option2PlaceOrderTestCase3_RemovePizza
- Intent: ensure remove-item sub-option works safely.
- Setup and Input: place-order flow with existing item, choose remove sub-option and target index/item.
- Expected Verification: selected item disappears from current order/cart summary.

10. option1_LoginSuccessful_Option2PlaceOrderTestCase4_ProceedToCheckoutConfirmYes
- Intent: confirm positive checkout decision completes order placement.
- Setup and Input: build a non-empty order, choose checkout, confirm with y/yes.
- Expected Verification: order is placed successfully, confirmation message appears, and order state transitions to expected active status.

11. option1_LoginSuccessful_Option2PlaceOrderTestCase5_ProceedToCheckoutEnterNShouldCancel
- Intent: ensure negative checkout decision aborts submission.
- Setup and Input: build a non-empty order, choose checkout, respond n/no.
- Expected Verification: no final order placement confirmation, and flow indicates checkout cancellation/return.

12. option1_LoginSuccessful_Option3ViewMyOrders_ShouldShowNoOrdersMessage
- Intent: verify empty-history handling in member order history view.
- Setup and Input: login member with no past orders and select option 3.
- Expected Verification: explicit no-orders message is shown instead of empty/invalid details.

13. option1_LoginSuccessful_Option3ViewMyOrders_ReorderPreviousOrder_ShouldTriggerReorderCallback
- Intent: validate reorder integration from order-history screen.
- Setup and Input: preload member with previous order(s), choose reorder path and confirm.
- Expected Verification: reorder callback/handler is invoked and flow proceeds as reorder action.

14. option1_LoginSuccessful_Option3ViewMyOrders_NotReorder_ShouldReturnWithoutTriggeringCallback
- Intent: ensure user can view history and exit without side effects.
- Setup and Input: open history with existing order and decline reorder.
- Expected Verification: no reorder callback invocation and clean return to member menu.

15. option1_LoginSuccessful_Option4SearchOrderById_ShouldShowPromptAndNotFound
- Intent: verify not-found search behavior for member search function.
- Setup and Input: choose search by order ID and enter missing/non-existing ID.
- Expected Verification: prompt is shown, then not-found message is printed, with no crash.

16. option1_LoginSuccessful_Option4SearchOrderById_ShouldShowOrderDetailsWhenSearchSuccessful
- Intent: confirm successful search renders full order details.
- Setup and Input: preload an order for current member and search using exact ID.
- Expected Verification: output includes order ID and detail fields (status/items/amount).

17. option1_LoginSuccessful_Option5ViewMemberInfo_ShouldDisplayMemberInformation
- Intent: ensure profile info screen returns accurate member data.
- Setup and Input: login as known member and select option 5.
- Expected Verification: member identity fields (username/name/phone or equivalent) appear in output.

18. option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldShowRecommendationFlow
- Intent: validate recommendation entry flow and prompt contract.
- Setup and Input: choose recommendation option after login.
- Expected Verification: recommendation result text is shown and user is asked whether to order recommendation.

19. option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldAllowNotOrderingRecommendedPizza
- Intent: ensure decline path works for recommendation feature.
- Setup and Input: open recommendation, respond with no.
- Expected Verification: no order placement side effect; flow returns to menu cleanly.

20. option1_LoginSuccessful_Option6GetPizzaRecommendations_ShouldAllowOrderingRecommendedPizza
- Intent: ensure accept path connects recommendation to order flow.
- Setup and Input: open recommendation, respond with yes to order.
- Expected Verification: recommended pizza is added/processed in order flow and corresponding confirmation appears.

21. option1_LoginSuccessful_Option2PlaceOrder_MinusOneAtAddNewPizza_ShouldBackToMenu
- Intent: verify -1 back behavior in add-new-pizza sub-wizard.
- Setup and Input: enter place-order add-item path, input -1 at add stage.
- Expected Verification: add operation is canceled and control returns to previous place-order menu without item insertion.

## Option 2 - Member Registration
Source: Option2MemberRegisterCommandTest.java

22. option2_RegisterSuccessful_ShouldRegisterNewMember
- Intent: verify complete registration happy path.
- Setup and Input: provide unique username, password, name, and valid phone.
- Expected Verification: new member is persisted and success message is shown.

23. option2_RegisterDuplicateUsername_ShouldRejectAndExit
- Intent: prevent duplicate identity creation.
- Setup and Input: attempt registration with an existing username.
- Expected Verification: duplicate warning/rejection shown, and no second member with same username is created.

24. option2_RegisterMinusOneAtUsername_ShouldGoBack
- Intent: support immediate cancel before data entry.
- Setup and Input: input -1 at username prompt.
- Expected Verification: registration flow exits back to previous screen.

25. option2_RegisterMinusOneAtPassword_ShouldGoBackToUsername
- Intent: validate step-level backtracking from password prompt.
- Setup and Input: enter username, then -1 for password.
- Expected Verification: command navigates back to username stage.

26. option2_RegisterEmptyUsername_ShouldRejectThenAllowNextInput
- Intent: enforce username non-empty validation while allowing retry.
- Setup and Input: submit empty username first, then valid username and remaining fields.
- Expected Verification: validation error appears for empty input; subsequent valid input proceeds successfully.

27. option2_RegisterMinusOneAtName_ShouldGoBackToPassword
- Intent: ensure mid-form back navigation from name prompt.
- Setup and Input: provide username and password, then input -1 at name.
- Expected Verification: flow returns to password step rather than terminating.

28. option2_RegisterMinusOneAtPhone_ShouldGoBackToName
- Intent: verify back navigation at final field.
- Setup and Input: reach phone prompt and enter -1.
- Expected Verification: flow returns to name step for correction.

29. option2_RegisterInvalidPhoneLengthAndNonDigit_ShouldRejectThenAcceptValidPhone
- Intent: validate strict phone formatting rules.
- Setup and Input: try invalid length and non-digit phone values, then provide valid phone.
- Expected Verification: invalid entries are rejected with validation message; valid phone is accepted and registration completes.

## Option 3 - Continue as Guest
Source: Option3ContinueAsGuestMenuTest.java

30. option3_MainMenu_ShouldDisplayContinueAsGuest
- Intent: verify guest entry point is visible at top-level menu.
- Setup and Input: render main menu.
- Expected Verification: continue-as-guest option text is present.

31. option3_GuestOrderSubOption1_AddNewPizza_ShouldBuildItem
- Intent: confirm guest can create an order item via add path.
- Setup and Input: choose guest flow, enter add-new-pizza sub-option with pizza config and quantity.
- Expected Verification: item is constructed and appears in guest cart summary.

32. option3_GuestOrderSubOption2_ModifyExistingPizza_ShouldUpdateQuantity
- Intent: verify guest can edit existing line quantity.
- Setup and Input: start with pre-added item, execute modify sub-option and set new quantity.
- Expected Verification: targeted item quantity is updated in resulting cart state/output.

33. option3_GuestOrderSubOption3_RemovePizza_ShouldRemoveItem
- Intent: ensure guest remove operation correctly updates cart.
- Setup and Input: create at least one item and choose remove sub-option.
- Expected Verification: selected line is removed and cart reflects reduced item count.

34. option3_GuestOrderSubOption4_ProceedCheckoutYes_ShouldPlaceOrderAndZeroPoints
- Intent: validate guest checkout completion and points rule.
- Setup and Input: prepare guest cart, choose checkout, confirm yes.
- Expected Verification: order placement succeeds and awarded points for guest are explicitly zero.

35. option3_GuestOrderSubOption4_ProceedCheckoutNo_ShouldCancelOrder
- Intent: verify guest can abort checkout before finalizing.
- Setup and Input: choose checkout and answer no.
- Expected Verification: no placed-order confirmation and flow returns/cancels gracefully.

36. option3_GuestAddNewPizza_MinusOne_ShouldBackWithoutAdding
- Intent: enforce back/cancel behavior in guest add-item wizard.
- Setup and Input: enter add-new-pizza then type -1.
- Expected Verification: wizard exits without creating new cart line.

37. option3_GuestContinueOrderFlow_InvalidPhone_ShouldValidateAndAllowCancelCheckout
- Intent: test validation robustness and later cancellation path in same scenario.
- Setup and Input: provide invalid phone during guest checkout sequence, then continue and cancel checkout.
- Expected Verification: phone validation message appears; user can still navigate and cancel safely.

## Option 4 - Search Order
Source: Option4SearchOrderCommandTest.java

38. option4_MemberSearchOwnOrder_ShouldDisplayOrderDetails
- Intent: verify authorized member search behavior.
- Setup and Input: create order owned by logged-in member; execute search with that order ID.
- Expected Verification: search succeeds and detailed order content is printed.

39. option4_GuestSearchMemberOrder_ShouldBeUnauthorized
- Intent: ensure access control denies guest viewing member orders.
- Setup and Input: as guest context, search for a member-owned order ID.
- Expected Verification: unauthorized response/message appears and details are not disclosed.

40. option4_GuestSearchGuestOrder_ShouldDisplayOrderDetails
- Intent: confirm guest can retrieve guest-owned order.
- Setup and Input: create a guest order and search using matching ID.
- Expected Verification: details for that guest order are displayed.

41. option4_SearchOrderByIdInputSuccess_GuestOrder_ShouldDisplayOrderDetails
- Intent: validate scanner-driven interactive input path (not direct setter injection).
- Setup and Input: provide order ID through scanner input stream for an existing guest order.
- Expected Verification: prompt is shown, entered ID is consumed, and matching order details appear.

42. option4_GuestSearchMinusOneOrderId_ShouldShowNotFound
- Intent: cover edge input of -1 as search ID.
- Setup and Input: execute search with -1.
- Expected Verification: flow treats it as non-existent order ID and returns not-found output.

43. option4_MemberSearchAnotherMembersOrder_ShouldBeUnauthorized
- Intent: enforce member-to-member data isolation.
- Setup and Input: authenticated member A searches order belonging to member B.
- Expected Verification: unauthorized message is shown; member B's order details are not returned.

## Option 5 - Employee Login and Employee/Manager Menu
Source: Option5EmployeeLoginCommandTest.java

44. option5_EmployeeLoginSuccessful_ShouldLoginWithValidCredentials
- Intent: confirm employee authentication happy path.
- Setup and Input: provide valid employee username/password.
- Expected Verification: login succeeds and employee session is established.

45. option5_EmployeeLoginFailed_ShouldRejectInvalidCredentials
- Intent: block invalid employee login attempts.
- Setup and Input: submit incorrect credentials.
- Expected Verification: rejection message is printed and no authenticated employee context is created.

46. option5_EmployeeLoginMinusOneAtUsername_ShouldGoBack
- Intent: support cancel at initial employee login step.
- Setup and Input: enter -1 at username prompt.
- Expected Verification: login flow returns to previous menu.

47. option5_EmployeeLoginMinusOneAtPassword_ShouldGoBackToUsername
- Intent: support backtracking at password step.
- Setup and Input: enter username, then -1 for password.
- Expected Verification: flow returns to username input stage.

48. option5_StaffMenu_ShouldDisplayAllSubOptions
- Intent: verify role-based menu rendering for staff.
- Setup and Input: login as staff and display employee menu.
- Expected Verification: staff-allowed options are shown; manager-only options are excluded.

49. option5_ManagerMenu_ShouldDisplayAllSubOptions
- Intent: verify manager receives extended menu set.
- Setup and Input: login as manager and display employee menu.
- Expected Verification: manager options include administrative features in addition to common options.

50. option5_StaffMenu_LogoutSubOption_ShouldReturnOne
- Intent: verify control flow return code for staff logout path.
- Setup and Input: staff selects logout option.
- Expected Verification: method returns code 1 (or defined logout return value).

51. option5_ManagerMenu_AccessAdminSubOption_ShouldReturnTwo
- Intent: verify manager admin-panel branch selector.
- Setup and Input: manager selects access-admin sub-option.
- Expected Verification: method returns code 2 (admin branch entry signal).

52. option5_ManagerMenu_LogoutSubOption_ShouldReturnOne
- Intent: ensure manager logout path mirrors expected control signal.
- Setup and Input: manager chooses logout option.
- Expected Verification: return value matches logout code and exits employee context.

53. option5_EmployeeMenuInvalidChoice_ShouldShowInvalidMessage
- Intent: guard menu against unsupported input.
- Setup and Input: choose invalid menu number/character.
- Expected Verification: invalid choice message shown and system remains stable.

54. option5_EmployeeMenuViewOrderSubOption_ShouldHandleNoProcessingOrders
- Intent: confirm empty processing queue is handled user-friendly.
- Setup and Input: no processing orders exist, select view-order sub-option.
- Expected Verification: no-processing-orders message appears and no exception occurs.

55. option5_EmployeeMenuViewOrderSubOption_ShouldFinishOrderSuccessfully
- Intent: verify order completion operation by employee.
- Setup and Input: preload processing order, select finish flow, confirm completion.
- Expected Verification: order status changes from processing to completed and success message appears.

56. option5_EmployeeMenuViewOrderSubOption_ShouldNotFinishOrderWhenChooseN
- Intent: verify refusal path keeps state unchanged.
- Setup and Input: choose finish action but respond no.
- Expected Verification: order remains processing and cancellation/no-change message is shown.

57. option5_EmployeeMenuSearchOrderSubOption_ShouldShowOrderNotFound
- Intent: validate not-found handling in employee search flow.
- Setup and Input: search with non-existent order ID.
- Expected Verification: not-found output appears with no crash.

58. option5_EmployeeMenuSearchOrderSubOption_ShouldShowOrderDetailsWhenSearchSuccessful
- Intent: validate successful employee order search rendering.
- Setup and Input: search using existing order ID.
- Expected Verification: order details section is printed correctly.

59. option5_EmployeeMenuCancelOrderSubOption_ShouldHandleNoProcessingOrders
- Intent: ensure cancel-order operation handles empty state safely.
- Setup and Input: run cancel sub-option when queue has no processing orders.
- Expected Verification: informative message shown and no state corruption.

60. option5_ManagerMenu_CancelProcessingOrder_ShouldCancelSuccessfully
- Intent: verify manager can cancel active processing order.
- Setup and Input: preload processing order, execute cancel flow, confirm yes.
- Expected Verification: status transitions to canceled and cancellation success output appears.

61. option5_ManagerMenu_CancelProcessingOrder_ShouldNotCancelWhenUserChooseN
- Intent: verify cancel flow no-branch preserves order.
- Setup and Input: execute cancel flow and choose no.
- Expected Verification: order remains processing and operation exits without cancellation.

62. option5_ManagerOption4_ManageCouponsCase1_AddFixedDiscountCoupon_ShouldAddSuccessfully
- Intent: verify manager can add fixed-amount coupon.
- Setup and Input: open coupon management case 1 and input fixed discount coupon data.
- Expected Verification: coupon is persisted with fixed discount type and success output is shown.

63. option5_ManagerOption4_ManageCouponsCase2_AddPercentageDiscountCoupon_ShouldAddSuccessfully
- Intent: verify manager can add percentage coupon.
- Setup and Input: open coupon management case 2 and provide percentage coupon data.
- Expected Verification: coupon is saved with percentage type and appears in coupon list/data.

64. option5_ManagerOption4_ManageCouponsCase3_ToggleCouponStatus_ShouldToggleSuccessfully
- Intent: validate enable/disable lifecycle for existing coupon.
- Setup and Input: choose coupon toggle action for existing code.
- Expected Verification: coupon active flag flips state and result message confirms update.

65. option5_ManagerOption5_AccessAdminPanelCase1_CreateStaff_ShouldCreateSuccessfully
- Intent: verify admin panel can create employee account.
- Setup and Input: manager enters admin panel create-staff flow with new staff credentials/profile.
- Expected Verification: new staff record is persisted and creation confirmation appears.

66. option5_ManagerOption5_AccessAdminPanelCase2_EditProcessingOrder_ShouldSaveAndExit
- Intent: verify manager can edit processing order and commit changes.
- Setup and Input: open admin edit-order flow, modify target fields, choose save and exit.
- Expected Verification: updated order state/details are persisted and flow exits successfully.

## Coverage Summary
- Functional breadth: covers all main menu options 1 to 5 and key sub-options for member, guest, employee, and manager roles.
- Branch coverage: includes success paths, failure paths, validation failures, unauthorized access, and -1 back-navigation paths.
- Stateful behavior: verifies status transitions (processing to completed/canceled), reorder callbacks, recommendation accept/decline, and coupon state toggling.
- Input realism: uses scanner-driven interactive input and output assertions to validate real CLI behavior, not only direct method calls.
