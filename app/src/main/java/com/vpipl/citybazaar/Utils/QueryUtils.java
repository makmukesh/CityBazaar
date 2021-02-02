package com.vpipl.citybazaar.Utils;

import android.content.Context;

import com.vpipl.citybazaar.AppController;

/**
 * Created by Mukesh on 27/12/2020.
 */
public class QueryUtils {
    public static String getViewgenealogyURL(Context con) {
        String url = "";
        try {
            url = AppUtils.ViewgenealogyURL() + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getViewBinarygenealogyURL(Context con) {
        String url = "";
        try {
            url = AppUtils.ViewBinarygenealogyURL() + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String methodCheckSponsor = "CheckSponsor";
    public static String methodToChangePassword = "ChangePassword";
    public static String methodToGetUserProfile = "ViewProfile";
    public static String methodToUpdateUserProfile = "UpdateProfile";
    public static String methodMaster_FillState = "Master_FillState";
    public static String methodMaster_FillBank = "Master_FillBank";
    public static String methodGet_BankDetail = "Get_BankDetail";
    public static String methodUploadImages = "UploadKYCImages";
    public static String methodGetImages = "ReadKYCImages";
    public static String methodWelcomeLetter = "WelcomeLetter";
    public static String methodToGetSponsorTeamDetail = "SponsorTeamDetail";
    public static String methodToGetMyDirectMembers = "MyDirectMembers";
    public static String methodToGetWalletTransactionReport = "WalletTransactionDetail";
    public static String methodToGetWalletBalance = "GetAvailableWalletBalance";
    public static String methodToRequestWalletAmount = "RequestForWalletAmount";
    public static String methodToGetWallet_Request_Status_Report = "WalletRequestReport";
    public static String methodToGetTeamRepurchaseSummary = "TeamRepurchaseBVSummary";
    public static String methodToGetRepurchaseBVSummaryDetail = "RepurchaseBVSummaryDetail";
    public static String methodToGetDashboardDetail = "Dashboard";
    public static String methodToForgetPasswordMember = "ForgotPassword";
    ////////////////////////////////////////
    public static String methodtoSendOTP = "SendGoogleOTP";
  ;
    public static String methodtoGetDrawerMenuItems = "DashBoardMenu";
    public static String methodToGetViewOrdersList = "MyOrders";
    public static String methodToGetViewOrdersDetails = "MyOrderDetils";
    public static String methodToWalletToBankTransferDetail = "WalletToBankTransferDetail";
    public static String methodToWebEncryption = "EncryptData";
    public static String methodToGetRepurchaseBillSummary = "RepurchaseBillSummary";
    public static String methodToGetTDSReport = "TDSReport";
    public static String methodToSubmitQuery = "RegisterEnquiryComplaint";
    public static String methodToOpenQueriesReport = "EnquiryComplaintDetails";
    public static String methodToClosedQueriesReport = "ViewClosedQueries";
    public static String methodToOpenQuerieConversation = "EnquiryComplaintQuerieDetails";
    public static String methodToProductWalletBalance = "ProductWalletBalance";
    public static String methodToBuniessCard = "BuniessCard";
    public static String methodToGetViewachiversList = "AllAchievers";
    public static String methodToGetRewardsNameList = "RewardsNameList";
    public static String methodToSendOTPOnUpdateProfile = "SendGoogleOTP";
    public static String methodToNewJoiningFree = "NewJoiningFree";
    public static String methodToCheckAppRunningStatus = "CheckAppRunningStatus";
    public static String methodToLoginMember = "LoginMember";
    public static String methodToFranchiseList = "FranchiseList";
    public static String methodToGetDownlineTeamDetail = "DownlineTeamDetail";
    public static String methodToGetAddedDailyPayout = "AddedDailyPayout";
    public static String methodToFillKitMaster = "Dwn_Session_List";
    public static String methodToDirectSponsorBonusDetail = "DirectSponsorBonusDetail";
    public static String methodToDownlineTeamAutoPoolIncomeDetail = "DownlineTeamAutoPoolIncomeDetail";
    public static String methodSponsorPageFillPackage = "SponsorPageFillPackage";
    public static String methodtoAutoGrowthpoolDetails = "AutoGrowthpoolDetails";
    public static String methodtoViewAutoGrowthpoolDetails = "ViewAutoGrowthpoolDetails";
    public static String methodtoDashBoardBottomData = "DashBoardBottomData";
    public static String methodSponsorActivationWeek = "SponsorActivationWeek";
    public static String methodSendMailForEnquiry = "SendMailForEnquiry";
    public static String methodDailyIncentive = "DailyIncentive";
    public static String methodDailyIncentiveDetailReport = "DailyIncentiveDetailsReport";
    public static String methodWeeklyIncentive = "WeeklyIncentive";
    public static String methodWeeklyIncentiveDetailReport = "WeeklyIncentiveDetails";
    public static String methodMonthlyIncentive = "MonthlyIncentive";
    public static String methodMonthlyIncentiveDetail = "MonthlyIncentiveDetailsReport";
    public static String methodMyRepurchaseBVDetailNew = "MyRepurchaseBVD`etailNew";
    public static String methodAddOrder_INMemberPanelOnline = "AddOrder_INMemberPanelOnline";
    public static String methodOnlineOrderAfterSucess = "AfterOrder_Payment";
    public static String methodAddOrder_INMemberPanel = "AddOrder_INMemberPanel";

    public static String methodTransactionLogin = "TransactionLogin";
    public static String methodToGetEpinRequestReport = "EpinRequestReport";
    public static String methodToPinRequestDetailsDateWise = "PinRequestDetailsDateWise";
    public static String  methodToPinReceiveReport = "PinReceiveReport";
    public static String  methodToPinReceiveReportDetails = "PinReceiveReportDetails";
    public static String methodToEPinDetail = "EPinDetail";
    public static String methodToGenerated_IssuedPinDetails_BillDetails = "Generated_IssuedPinDetails_BillDetails";
    public static String methodtoPinTransferDetails = "PinTransferDetails";
    public static String methodtoLoadBillType = "LoadBillType";
    public static String methodtoGenerated_IssuedPinDetails = "Generated_IssuedPinDetails";
    public static String methodToRequestEpin = "RequestEPin";
    public static String methodPinTransferReport = "PinTransferReport";
    public static String methodToPinRequestPackage = "PinRequestPackage";
    public static String methodToEPinTransferLoadPackage = "EPinTransferLoadPackage";
    public static String methodCheckEPinTransferMember = "CheckEPinTransferMember";
    public static String methodEPinTransfer = "EPinTransfer";
    public static String methodCheckTopupIDNo = "CheckTopupIDNo";
    public static String methodTopupMember = "TopupMember";
    public static String methodToSelectSliderImages = "SelectSliderImages";
    public static String methodToRewardDetails = "RewardDetails";
    public static String methodDashboardDetails = "DashboardDetailNew";
    public static String methodSelect_Advertisment = "Select_Advertisment";
    public static String methodShareSponsorLink = "ShareSponsorLink";
    public static String methodToSelect_Category = "Select_Category";
    public static String methodToSelect_Gallary = "Select_Gallary";

}