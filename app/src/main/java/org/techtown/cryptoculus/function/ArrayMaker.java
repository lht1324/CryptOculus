package org.techtown.cryptoculus.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.techtown.cryptoculus.R;
import org.techtown.cryptoculus.coinInfo.CoinInfoBithumb;
import org.techtown.cryptoculus.coinInfo.CoinInfoCoinone;
import org.techtown.cryptoculus.coinInfo.CoinInfoHuobi;
import org.techtown.cryptoculus.currencys.CurrencysBithumb;
import org.techtown.cryptoculus.currencys.CurrencysCoinone;
import org.techtown.cryptoculus.currencys.CurrencysHuobi;
import org.techtown.cryptoculus.ticker.TickerHuobi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ArrayMaker {
    private boolean restartApp;
    private boolean refreshedCoinone;
    private boolean refreshedBithumb;
    private boolean refreshedHuobi;
    private ArrayList<CoinInfoCoinone> coinInfosCoinone;
    private ArrayList<CoinInfoBithumb> coinInfosBithumb;
    private ArrayList<CoinInfoHuobi> coinInfosHuobi;
    private Context mContext;

    public ArrayMaker(boolean restartApp, boolean refreshedCoinone, boolean refreshedBithumb, boolean refreshedHuobi, ArrayList<CoinInfoCoinone> coinInfosCoinone, ArrayList<CoinInfoBithumb> coinInfosBithumb, ArrayList<CoinInfoHuobi> coinInfosHuobi, Context mContext) {
        this.restartApp = restartApp;
        this.refreshedCoinone = refreshedCoinone;
        this.refreshedBithumb = refreshedBithumb;
        this.refreshedHuobi = refreshedHuobi;
        this.coinInfosCoinone = coinInfosCoinone;
        this.coinInfosBithumb = coinInfosBithumb;
        this.coinInfosHuobi = coinInfosHuobi;
        this.mContext = mContext;
    }

    public ArrayList<CoinInfoCoinone> makeArrayCoinone(CurrencysCoinone currencyList) {
        ArrayList<CoinInfoCoinone> coinInfos = new ArrayList<CoinInfoCoinone>();
        SharedPreferences pref = mContext.getSharedPreferences("saveCoinone", MODE_PRIVATE);

        coinInfos.add(new CoinInfoCoinone(currencyList.btc, "????????????", R.drawable.btc));
        coinInfos.add(new CoinInfoCoinone(currencyList.eth, "????????????", R.drawable.eth));
        coinInfos.add(new CoinInfoCoinone(currencyList.xrp, "??????", R.drawable.xrp));
        coinInfos.add(new CoinInfoCoinone(currencyList.bch, "???????????? ??????", R.drawable.bch));
        coinInfos.add(new CoinInfoCoinone(currencyList.eos, "?????????", R.drawable.eos));
        coinInfos.add(new CoinInfoCoinone(currencyList.bsv, "???????????????????????????", R.drawable.bsv));
        coinInfos.add(new CoinInfoCoinone(currencyList.etc, "???????????? ?????????", R.drawable.etc));
        coinInfos.add(new CoinInfoCoinone(currencyList.ltc, "???????????????", R.drawable.ltc));
        coinInfos.add(new CoinInfoCoinone(currencyList.prom, "??????????????????", R.drawable.prom));
        coinInfos.add(new CoinInfoCoinone(currencyList.atom, "??????????????????", R.drawable.atom));
        coinInfos.add(new CoinInfoCoinone(currencyList.xtz, "?????????", R.drawable.xtz));
        coinInfos.add(new CoinInfoCoinone(currencyList.psc, "???????????????", R.drawable.basic)); //
        coinInfos.add(new CoinInfoCoinone(currencyList.pci, "????????????", R.drawable.pci));
        coinInfos.add(new CoinInfoCoinone(currencyList.trx, "??????", R.drawable.trx));
        coinInfos.add(new CoinInfoCoinone(currencyList.fleta, "?????????", R.drawable.fleta));
        coinInfos.add(new CoinInfoCoinone(currencyList.qtum, "??????", R.drawable.qtum));
        coinInfos.add(new CoinInfoCoinone(currencyList.luna, "??????", R.drawable.luna));
        coinInfos.add(new CoinInfoCoinone(currencyList.knc, "?????????", R.drawable.knc));
        coinInfos.add(new CoinInfoCoinone(currencyList.kvi, "??????????????????", R.drawable.basic)); //
        coinInfos.add(new CoinInfoCoinone(currencyList.egg, "????????????", R.drawable.egg));
        coinInfos.add(new CoinInfoCoinone(currencyList.bna, "????????????", R.drawable.bna));
        coinInfos.add(new CoinInfoCoinone(currencyList.xlm, "???????????????", R.drawable.xlm));
        coinInfos.add(new CoinInfoCoinone(currencyList.iota, "????????????", R.drawable.iota));
        coinInfos.add(new CoinInfoCoinone(currencyList.xpn, "?????????X", R.drawable.xpn));
        coinInfos.add(new CoinInfoCoinone(currencyList.gas, "??????", R.drawable.gas));
        coinInfos.add(new CoinInfoCoinone(currencyList.ogn, "????????? ????????????", R.drawable.ogn));
        coinInfos.add(new CoinInfoCoinone(currencyList.ong, "??????????????????", R.drawable.ong));
        coinInfos.add(new CoinInfoCoinone(currencyList.chz, "?????????", R.drawable.chz));
        coinInfos.add(new CoinInfoCoinone(currencyList.data, "????????????", R.drawable.data));
        coinInfos.add(new CoinInfoCoinone(currencyList.soc, "????????????", R.drawable.soc));
        coinInfos.add(new CoinInfoCoinone(currencyList.zil, "?????????", R.drawable.zil));
        coinInfos.add(new CoinInfoCoinone(currencyList.bat, "????????????????????????", R.drawable.bat));
        coinInfos.add(new CoinInfoCoinone(currencyList.zrx, "????????????", R.drawable.zrx));
        coinInfos.add(new CoinInfoCoinone(currencyList.pxl, "??????????????????", R.drawable.pxl));
        coinInfos.add(new CoinInfoCoinone(currencyList.isr, "????????????", R.drawable.isr));
        coinInfos.add(new CoinInfoCoinone(currencyList.neo, "??????", R.drawable.neo));
        coinInfos.add(new CoinInfoCoinone(currencyList.redi, "??????", R.drawable.redi));
        coinInfos.add(new CoinInfoCoinone(currencyList.mbl, "????????????", R.drawable.mbl));
        coinInfos.add(new CoinInfoCoinone(currencyList.omg, "????????????", R.drawable.omg));
        coinInfos.add(new CoinInfoCoinone(currencyList.btt, "???????????????", R.drawable.btt));
        coinInfos.add(new CoinInfoCoinone(currencyList.drm, "???????????????", R.drawable.basic)); //
        coinInfos.add(new CoinInfoCoinone(currencyList.spin, "??????????????????", R.drawable.spin));
        coinInfos.add(new CoinInfoCoinone(currencyList.ankr, "?????? ????????????", R.drawable.ankr));
        coinInfos.add(new CoinInfoCoinone(currencyList.stpt, "????????????", R.drawable.stpt));
        coinInfos.add(new CoinInfoCoinone(currencyList.ont, "????????????", R.drawable.ont));
        coinInfos.add(new CoinInfoCoinone(currencyList.matic, "?????? ????????????", R.drawable.matic));
        coinInfos.add(new CoinInfoCoinone(currencyList.temco, "??????", R.drawable.temco));
        coinInfos.add(new CoinInfoCoinone(currencyList.ftm, "??????", R.drawable.ftm));
        coinInfos.add(new CoinInfoCoinone(currencyList.iotx, "???????????????", R.drawable.iotx));
        coinInfos.add(new CoinInfoCoinone(currencyList.abl, "????????????", R.drawable.abl));
        coinInfos.add(new CoinInfoCoinone(currencyList.pib, "??????", R.drawable.pib));
        coinInfos.add(new CoinInfoCoinone(currencyList.amo, "????????????", R.drawable.amo));
        coinInfos.add(new CoinInfoCoinone(currencyList.troy, "?????????", R.drawable.troy));
        coinInfos.add(new CoinInfoCoinone(currencyList.clb, "??????????????????", R.drawable.clb));
        coinInfos.add(new CoinInfoCoinone(currencyList.orbs, "?????????", R.drawable.orbs));
        coinInfos.add(new CoinInfoCoinone(currencyList.baas, "???????????????", R.drawable.baas));
        coinInfos.add(new CoinInfoCoinone(currencyList.hint, "????????????", R.drawable.hint));
        coinInfos.add(new CoinInfoCoinone(currencyList.hibs, "???????????????", R.drawable.hibs));
        coinInfos.add(new CoinInfoCoinone(currencyList.dad, "??????", R.drawable.dad));
        coinInfos.add(new CoinInfoCoinone(currencyList.uos, "?????????", R.drawable.uos));
        coinInfos.add(new CoinInfoCoinone(currencyList.btg, "???????????? ??????", R.drawable.btg));
        coinInfos.add(new CoinInfoCoinone(currencyList.arpa, "?????? ??????", R.drawable.arpa));
        coinInfos.add(new CoinInfoCoinone(currencyList.axl, "?????????", R.drawable.axl));
        coinInfos.add(new CoinInfoCoinone(currencyList.hum, "??????????????????", R.drawable.hum));
        coinInfos.add(new CoinInfoCoinone(currencyList.ksc, "?????????????????????", R.drawable.ksc));
        coinInfos.add(new CoinInfoCoinone(currencyList.wiken, "??????", R.drawable.wiken));

        if (restartApp && !pref.getBoolean("isEmptyCoinone", true)) {
            int[] getPositions = new int[coinInfos.size()];
            ArrayList<CoinInfoCoinone> temp = new ArrayList<CoinInfoCoinone>();

            for (int i = 0; i < coinInfos.size(); i++) {
                getPositions[i] = pref.getInt(coinInfos.get(i).getCoinName() + "position", 0);
                temp.add(null);
            }

            for (int i = 0; i < coinInfos.size(); i++)
                temp.set(getPositions[i], coinInfos.get(i));

            coinInfos = temp;

            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(pref.getBoolean(coinInfos.get(i).getCoinName(), true));

            if (!isEmpty(coinInfosCoinone) && !isEmpty(coinInfosBithumb) && !isEmpty(coinInfosHuobi))
                restartApp = false;
        }

        if (refreshedCoinone) {
            for (int i = 0; i < coinInfos.size(); i++) {
                String temp1 = coinInfosCoinone.get(i).getCoinName(); // ?????? ???????????? ??? ??? ??????????
                // int??? position ????????? ?????? ????????? ????????? position??? ???????????? ????????? ????????????
                String temp2 = coinInfos.get(i).getCoinName();

                if (!(temp1.equals(temp2))) { // ????????? ?????? ???
                    for (int j = 0; j < coinInfos.size(); j++) {
                        if (temp1.equals(coinInfos.get(j).getCoinName())) {
                            Collections.swap(coinInfos, i, j);
                        }
                    }
                }
            }
            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(coinInfosCoinone.get(i).getCoinViewCheck());
        }

        return coinInfos;
    }

    public ArrayList<CoinInfoBithumb> makeArrayBithumb(CurrencysBithumb currencyList) {
        ArrayList<CoinInfoBithumb> coinInfos = new ArrayList<CoinInfoBithumb>();
        SharedPreferences pref = mContext.getSharedPreferences("saveBithumb", MODE_PRIVATE);

        coinInfos.add(new CoinInfoBithumb(currencyList.XRP, "XRP / ??????", R.drawable.xrp));
        coinInfos.add(new CoinInfoBithumb(currencyList.BTC, "BTC / ????????????", R.drawable.btc));
        coinInfos.add(new CoinInfoBithumb(currencyList.ETH, "ETH / ????????????", R.drawable.eth));
        coinInfos.add(new CoinInfoBithumb(currencyList.XLM, "XLM / ???????????????", R.drawable.xlm));
        coinInfos.add(new CoinInfoBithumb(currencyList.EOS, "EOS / ?????????", R.drawable.eos));
        coinInfos.add(new CoinInfoBithumb(currencyList.MBL, "MBL / ????????????", R.drawable.mbl));
        coinInfos.add(new CoinInfoBithumb(currencyList.BNP, "BNP / ?????????", R.drawable.bnp)); // ?????? ??????
        coinInfos.add(new CoinInfoBithumb(currencyList.BSV, "BSV / ????????????????????????", R.drawable.bsv));
        coinInfos.add(new CoinInfoBithumb(currencyList.XSR, "XSR / ??????", R.drawable.xsr));
        coinInfos.add(new CoinInfoBithumb(currencyList.WICC, "WICC / ???????????????", R.drawable.wicc));
        coinInfos.add(new CoinInfoBithumb(currencyList.BCH, "BCH / ???????????? ??????", R.drawable.bch));
        coinInfos.add(new CoinInfoBithumb(currencyList.MCO, "MCO / ???????????????", R.drawable.mco));
        coinInfos.add(new CoinInfoBithumb(currencyList.REP, "REP / ??????", R.drawable.rep));
        coinInfos.add(new CoinInfoBithumb(currencyList.LTC, "LTC / ???????????????", R.drawable.ltc));
        coinInfos.add(new CoinInfoBithumb(currencyList.TRX, "TRX / ??????", R.drawable.trx));
        coinInfos.add(new CoinInfoBithumb(currencyList.POWR, "POWR / ????????????", R.drawable.powr));
        coinInfos.add(new CoinInfoBithumb(currencyList.WAVES, "WAVES / ?????????", R.drawable.waves));
        coinInfos.add(new CoinInfoBithumb(currencyList.AMO, "AMO / ????????????", R.drawable.amo));
        coinInfos.add(new CoinInfoBithumb(currencyList.SOC, "SOC / ????????????", R.drawable.soc));
        coinInfos.add(new CoinInfoBithumb(currencyList.AE, "AE / ????????????", R.drawable.ae));
        coinInfos.add(new CoinInfoBithumb(currencyList.FLETA, "FLETA / ?????????", R.drawable.fleta));
        coinInfos.add(new CoinInfoBithumb(currencyList.APIX, "APIX / ?????????", R.drawable.apix)); // ??????
        coinInfos.add(new CoinInfoBithumb(currencyList.BCD, "BCD / ???????????? ???????????????", R.drawable.bcd));
        coinInfos.add(new CoinInfoBithumb(currencyList.STRAT, "STRAT / ???????????????", R.drawable.strat));
        coinInfos.add(new CoinInfoBithumb(currencyList.ZEC, "ZEC / ????????????", R.drawable.zec));
        coinInfos.add(new CoinInfoBithumb(currencyList.ANKR, "ANKR / ??????", R.drawable.ankr));
        coinInfos.add(new CoinInfoBithumb(currencyList.BAT, "BAT / ????????????????????????", R.drawable.bat));
        coinInfos.add(new CoinInfoBithumb(currencyList.GXC, "GXC / ???????????????", R.drawable.gxc));
        coinInfos.add(new CoinInfoBithumb(currencyList.WPX, "WPX / ??????????????????", R.drawable.wpx));
        coinInfos.add(new CoinInfoBithumb(currencyList.ETC, "ETC / ???????????? ?????????", R.drawable.etc));
        coinInfos.add(new CoinInfoBithumb(currencyList.HDAC, "HDAC / ????????????", R.drawable.hdac));
        coinInfos.add(new CoinInfoBithumb(currencyList.THETA, "THETA / ????????????", R.drawable.theta));
        coinInfos.add(new CoinInfoBithumb(currencyList.SXP, "SXP / ????????????", R.drawable.sxp));
        coinInfos.add(new CoinInfoBithumb(currencyList.ADA, "ADA / ?????????", R.drawable.ada));
        coinInfos.add(new CoinInfoBithumb(currencyList.DASH, "DASH / ??????", R.drawable.dash));
        coinInfos.add(new CoinInfoBithumb(currencyList.XMR, "XMR / ?????????", R.drawable.xmr));
        coinInfos.add(new CoinInfoBithumb(currencyList.LINK, "LINK / ????????????", R.drawable.link));
        coinInfos.add(new CoinInfoBithumb(currencyList.WAXP, "WAXP / ??????", R.drawable.waxp));
        coinInfos.add(new CoinInfoBithumb(currencyList.KNC, "KNC / ????????? ????????????", R.drawable.knc));
        coinInfos.add(new CoinInfoBithumb(currencyList.VET, "VET / ?????????", R.drawable.vet));
        coinInfos.add(new CoinInfoBithumb(currencyList.BTT, "BTT / ???????????????", R.drawable.btt));
        coinInfos.add(new CoinInfoBithumb(currencyList.ZIL, "ZIL / ?????????", R.drawable.zil));
        coinInfos.add(new CoinInfoBithumb(currencyList.AOA, "AOA / ?????????", R.drawable.aoa));
        coinInfos.add(new CoinInfoBithumb(currencyList.ITC, "ITC / ??????????????????", R.drawable.itc));
        coinInfos.add(new CoinInfoBithumb(currencyList.LUNA, "LUNA / ??????", R.drawable.luna));
        coinInfos.add(new CoinInfoBithumb(currencyList.QTUM, "QTUM / ??????", R.drawable.qtum));
        coinInfos.add(new CoinInfoBithumb(currencyList.MTL, "MTL / ??????", R.drawable.mtl));
        coinInfos.add(new CoinInfoBithumb(currencyList.ORBS, "ORBS / ?????????", R.drawable.orbs));
        coinInfos.add(new CoinInfoBithumb(currencyList.FAB, "FAB / ?????????", R.drawable.fab));
        coinInfos.add(new CoinInfoBithumb(currencyList.BTG, "BTG / ???????????? ??????", R.drawable.btg));
        coinInfos.add(new CoinInfoBithumb(currencyList.TMTG, "TMTG / ???????????????????????????", R.drawable.tmtg));
        coinInfos.add(new CoinInfoBithumb(currencyList.FCT, "FCT / ???????????????", R.drawable.fct));
        coinInfos.add(new CoinInfoBithumb(currencyList.FNB, "FNB / ????????????????????????", R.drawable.fnb));
        coinInfos.add(new CoinInfoBithumb(currencyList.ICX, "ICX / ?????????", R.drawable.icx));
        coinInfos.add(new CoinInfoBithumb(currencyList.LRC, "LRC / ?????????", R.drawable.lrc));
        coinInfos.add(new CoinInfoBithumb(currencyList.LOOM, "LOOM / ???????????????", R.drawable.loom));
        coinInfos.add(new CoinInfoBithumb(currencyList.IPX, "IPX / ?????????????????????", R.drawable.ipx));
        coinInfos.add(new CoinInfoBithumb(currencyList.NPXS, "NPXS / ????????????", R.drawable.npxs));
        coinInfos.add(new CoinInfoBithumb(currencyList.IOST, "IOST / ????????????", R.drawable.iost));
        coinInfos.add(new CoinInfoBithumb(currencyList.FZZ, "FZZ / ????????????", R.drawable.fzz));
        coinInfos.add(new CoinInfoBithumb(currencyList.DAD, "DAD / ??????", R.drawable.dad));
        coinInfos.add(new CoinInfoBithumb(currencyList.CON, "CON / ??????", R.drawable.conun));
        coinInfos.add(new CoinInfoBithumb(currencyList.BZNT, "BZNT / ?????????", R.drawable.bznt));
        coinInfos.add(new CoinInfoBithumb(currencyList.TRUE, "TRUE / ????????????", R.drawable.truechain));
        coinInfos.add(new CoinInfoBithumb(currencyList.EM, "EM / ????????????", R.drawable.em));
        coinInfos.add(new CoinInfoBithumb(currencyList.ENJ, "ENJ / ????????????", R.drawable.enj));
        coinInfos.add(new CoinInfoBithumb(currencyList.ELF, "ELF / ??????", R.drawable.elf));
        coinInfos.add(new CoinInfoBithumb(currencyList.FX, "FX / ????????????", R.drawable.fx));
        coinInfos.add(new CoinInfoBithumb(currencyList.WET, "WET / ????????????", R.drawable.wet));
        coinInfos.add(new CoinInfoBithumb(currencyList.PCM, "PCM / ????????????", R.drawable.pcm));
        coinInfos.add(new CoinInfoBithumb(currencyList.DVP, "DVP / ????????????", R.drawable.dvp));
        coinInfos.add(new CoinInfoBithumb(currencyList.SNT, "SNT / ?????????????????????????????????", R.drawable.snt));
        coinInfos.add(new CoinInfoBithumb(currencyList.CTXC, "CTXC / ????????????", R.drawable.ctxc));
        coinInfos.add(new CoinInfoBithumb(currencyList.HYC, "HYC / ?????????", R.drawable.hyc));
        coinInfos.add(new CoinInfoBithumb(currencyList.MIX, "MIX / ????????????", R.drawable.mix));
        coinInfos.add(new CoinInfoBithumb(currencyList.MXC, "MXC / ???????????????????????????", R.drawable.mxc));
        coinInfos.add(new CoinInfoBithumb(currencyList.CRO, "CRO / ?????????????????????", R.drawable.cro));
        coinInfos.add(new CoinInfoBithumb(currencyList.WOM, "WOM / ?????????", R.drawable.wom));
        coinInfos.add(new CoinInfoBithumb(currencyList.PIVX, "PIVX / ?????????", R.drawable.pivx));
        coinInfos.add(new CoinInfoBithumb(currencyList.INS, "INS / ???????????????", R.drawable.ins));
        coinInfos.add(new CoinInfoBithumb(currencyList.OMG, "OMG / ????????????", R.drawable.omg));
        coinInfos.add(new CoinInfoBithumb(currencyList.QKC, "QKC / ????????????", R.drawable.qkc));
        coinInfos.add(new CoinInfoBithumb(currencyList.OGO, "OGO / ?????????", R.drawable.ogo));
        coinInfos.add(new CoinInfoBithumb(currencyList.CHR, "CHR / ????????????", R.drawable.chr));
        coinInfos.add(new CoinInfoBithumb(currencyList.DAC, "DAC / ?????????", R.drawable.dac));
        coinInfos.add(new CoinInfoBithumb(currencyList.STEEM, "STEEM / ??????", R.drawable.steem));
        coinInfos.add(new CoinInfoBithumb(currencyList.VALOR, "VALOR / ????????????", R.drawable.valor));
        coinInfos.add(new CoinInfoBithumb(currencyList.LBA, "LBA / ?????????", R.drawable.lba));
        coinInfos.add(new CoinInfoBithumb(currencyList.TRV, "TRV / ??????????????????", R.drawable.trv));
        coinInfos.add(new CoinInfoBithumb(currencyList.XVG, "XVG / ??????", R.drawable.xvg));
        coinInfos.add(new CoinInfoBithumb(currencyList.GNT, "GNT / ??????", R.drawable.gnt));
        coinInfos.add(new CoinInfoBithumb(currencyList.LAMB, "LAMB / ??????", R.drawable.lamb));
        coinInfos.add(new CoinInfoBithumb(currencyList.RNT, "RNT / ????????? ????????????", R.drawable.rnt));
        coinInfos.add(new CoinInfoBithumb(currencyList.ZRX, "ZRX / ????????????", R.drawable.zrx));
        coinInfos.add(new CoinInfoBithumb(currencyList.WTC, "WTC / ????????????", R.drawable.wtc));
        coinInfos.add(new CoinInfoBithumb(currencyList.XEM, "XEM / ???", R.drawable.xem));
        coinInfos.add(new CoinInfoBithumb(currencyList.BHP, "BHP / ???????????????", R.drawable.bhp));

        if (restartApp && !pref.getBoolean("isEmptyBithumb", true)) {
            int[] getPositions = new int[coinInfos.size()];
            ArrayList<CoinInfoBithumb> temp = new ArrayList<CoinInfoBithumb>();

            for (int i = 0; i < coinInfos.size(); i++) {
                getPositions[i] = pref.getInt(coinInfos.get(i).getCoinName() + "position", 0);
                temp.add(null);
            }

            for (int i = 0; i < coinInfos.size(); i++)
                temp.set(getPositions[i], coinInfos.get(i));

            coinInfos = temp;

            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(pref.getBoolean(coinInfos.get(i).getCoinName(), true));

            if (!isEmpty(coinInfosCoinone) && !isEmpty(coinInfosBithumb) && !isEmpty(coinInfosHuobi))
                restartApp = false;
        }

        if (refreshedBithumb) {
            for (int i = 0; i < coinInfos.size(); i++) {
                String temp1 = coinInfosBithumb.get(i).getCoinName();
                String temp2 = coinInfos.get(i).getCoinName();

                if (!(temp1.equals(temp2))) { // ????????? ?????? ???
                    for (int j = i; j < coinInfos.size(); j++) {
                        if (temp1.equals(coinInfos.get(j).getCoinName()))
                            Collections.swap(coinInfos, i, j);
                    }
                }
            }

            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(coinInfosBithumb.get(i).getCoinViewCheck());
        }

        return coinInfos;
    }

    public ArrayList<CoinInfoHuobi> makeArrayHuobi(ArrayList<TickerHuobi> tickersHuobi) {
        ArrayList<CoinInfoHuobi> coinInfos = new ArrayList<CoinInfoHuobi>();
        CurrencysHuobi currencysHuobi = new CurrencysHuobi();
        SharedPreferences pref = mContext.getSharedPreferences("saveHuobi", MODE_PRIVATE);

        for (int i = 0; i < tickersHuobi.size(); i++) {
            TickerHuobi ticker = tickersHuobi.get(i);

            if (ticker.symbol.contains("grs"))
                currencysHuobi.grs = ticker;
            if (ticker.symbol.contains("xrp"))
                currencysHuobi.xrp = ticker;
            if (ticker.symbol.contains("eth"))
                currencysHuobi.eth = ticker;
            if (ticker.symbol.contains("mvl"))
                currencysHuobi.mvl = ticker;
            if (ticker.symbol.contains("ada"))
                currencysHuobi.ada = ticker;
            if (ticker.symbol.contains("fit"))
                currencysHuobi.fit = ticker;
            if (ticker.symbol.contains("bsv"))
                currencysHuobi.bsv = ticker;
            if (ticker.symbol.contains("btm"))
                currencysHuobi.btm = ticker;
            if (ticker.symbol.contains("ht"))
                currencysHuobi.ht = ticker;
            if (ticker.symbol.contains("usdt"))
                currencysHuobi.usdt = ticker;
            if (ticker.symbol.contains("iost"))
                currencysHuobi.iost = ticker;
            if (ticker.symbol.contains("ont"))
                currencysHuobi.ont = ticker;
            if (ticker.symbol.contains("pci"))
                currencysHuobi.pci = ticker;
            if (ticker.symbol.contains("solve"))
                currencysHuobi.solve = ticker;
            if (ticker.symbol.contains("uip"))
                currencysHuobi.uip = ticker;
            if (ticker.symbol.contains("xlm"))
                currencysHuobi.xlm = ticker;
            if (ticker.symbol.contains("ltc"))
                currencysHuobi.ltc = ticker;
            if (ticker.symbol.contains("eos"))
                currencysHuobi.eos = ticker;
            if (ticker.symbol.contains("skm"))
                currencysHuobi.skm = ticker;
            if (ticker.symbol.contains("btc"))
                currencysHuobi.btc = ticker;
            if (ticker.symbol.contains("bch"))
                currencysHuobi.bch = ticker;
            if (ticker.symbol.contains("trx"))
                currencysHuobi.trx = ticker;
        }

        coinInfos.add(new CoinInfoHuobi(currencysHuobi.btc, "BTC / ????????????", R.drawable.btc));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.usdt, "USDT / ??????", R.drawable.usdt));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.eth, "ETH / ????????????", R.drawable.eth));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.eos, "EOS / ?????????", R.drawable.eos));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.xrp, "XRP / ??????", R.drawable.xrp));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.pci, "PCI / ????????????", R.drawable.pci));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.bch, "BCH / ??????????????????", R.drawable.bch));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.bsv, "BSV / ????????????SV", R.drawable.bsv));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.ada, "ADA / ????????????", R.drawable.ada));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.ht, "HT / ???????????????", R.drawable.ht));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.trx, "TRX / ??????", R.drawable.trx));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.xlm, "XLM / ???????????????", R.drawable.xlm));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.ltc, "LTC / ???????????????", R.drawable.ltc));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.iost, "IOST / ?????????????????????", R.drawable.iost));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.ont, "ONT / ????????????", R.drawable.ont));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.grs, "GRS / ??????????????????", R.drawable.grs));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.btm, "BTM / ?????????", R.drawable.btm));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.solve, "SOLVE / ????????????", R.drawable.solve));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.uip, "UIP / ???????????????IP", R.drawable.uip));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.mvl, "MVL / ??????", R.drawable.mvl));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.fit, "FIT / FIT ??????", R.drawable.fit));
        coinInfos.add(new CoinInfoHuobi(currencysHuobi.skm, "SKM / ???????????? ????????????", R.drawable.skm));

        for (int i = 0; i < coinInfos.size(); i++) { // Huobi API ?????? ???????????? ?????? ?????? ?????? (E7 = * 10000000)
            TickerHuobi ticker = coinInfos.get(i).getCoinData();

            if (ticker.open.contains("E7"))
                ticker.open = Double.toString((Double.parseDouble(ticker.open.replace("E7", "")) * 10000000));
            if (ticker.close.contains("E7"))
                ticker.close = Double.toString((Double.parseDouble(ticker.close.replace("E7", "")) * 10000000));
            if (ticker.high.contains("E7"))
                ticker.high = Double.toString((Double.parseDouble(ticker.high.replace("E7", "")) * 10000000));
            if (ticker.low.contains("E7"))
                ticker.low = Double.toString((Double.parseDouble(ticker.low.replace("E7", "")) * 10000000));

            coinInfos.get(i).setCoinData(ticker);
        }

        if (restartApp && !pref.getBoolean("isEmptyHuobi", true)) {
            int[] getPositions = new int[coinInfos.size()];
            ArrayList<CoinInfoHuobi> temp = new ArrayList<CoinInfoHuobi>();

            for (int i = 0; i < coinInfos.size(); i++) {
                getPositions[i] = pref.getInt(coinInfos.get(i).getCoinName() + "position", 0);
                println("getPositions[" + i + "] = " + getPositions[i]);
                temp.add(null);
            }

            for (int i = 0; i < coinInfos.size(); i++)
                temp.set(getPositions[i], coinInfos.get(i));

            coinInfos = temp;

            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(pref.getBoolean(coinInfos.get(i).getCoinName(), true)); // ?????? ??????

            if (!isEmpty(coinInfosCoinone) && !isEmpty(coinInfosBithumb) && !isEmpty(coinInfosHuobi))
                restartApp = false;
        }

        if (refreshedHuobi) {
            for (int i = 0; i < coinInfos.size(); i++) {
                String temp1 = coinInfosHuobi.get(i).getCoinName();
                String temp2 = coinInfos.get(i).getCoinName();

                if (!(temp1.equals(temp2))) { // ????????? ?????? ???
                    for (int j = i; j < coinInfos.size(); j++) {
                        if (temp1.equals(coinInfos.get(j).getCoinName()))
                            Collections.swap(coinInfos, i, j);
                    }
                }
            }

            for (int i = 0; i < coinInfos.size(); i++)
                coinInfos.get(i).setCoinViewCheck(coinInfosHuobi.get(i).getCoinViewCheck());
        }

        return coinInfos;
    }

    public static boolean isEmpty(Object object) {
        if (object == null)
            return true;

        if ((object instanceof String) && (((String) object).trim().length() == 0))
            return true;

        if (object instanceof Map)
            return ((Map<?, ?>) object).isEmpty();

        if (object instanceof List)
            return ((List<?>) object).isEmpty();

        if (object instanceof Object[]) {
            return (((Object[]) object).length == 0);
        }
        return false;
    }

    public void println(String data) {
        Log.d("MainActivity", data);
    }
}
